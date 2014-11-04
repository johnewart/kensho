package net.johnewart.kensho.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Optional;
import net.johnewart.shuzai.DataFrame;
import net.johnewart.shuzai.Frequency;
import net.johnewart.shuzai.SampleMethod;
import net.johnewart.shuzai.TimeSeries;
import net.johnewart.kensho.core.*;
import net.johnewart.kensho.data.CustomTimeSeriesSerializer;
import net.johnewart.kensho.stats.QueryCache;
import net.johnewart.kensho.stats.StatsEngine;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DataResource {
    private static final Logger LOG = LoggerFactory.getLogger(DataResource.class);
    private final ObjectMapper mapper;
    private final StatsEngine statsEngine;

    public DataResource(StatsEngine statsEngine) {
        this.mapper = new ObjectMapper();
        this.statsEngine = statsEngine;

        SimpleModule module = new SimpleModule();
        module.addSerializer(TimeSeries.class, new CustomTimeSeriesSerializer());
        mapper.registerModule(module);
    }

    @GET
    @Timed
    @Path("running-queries")
    public List<RunningQuery> runningQueries() {
        return statsEngine.status.getRunningQueryList();
    }

    @GET
    @Timed
    @Path("long-running-queries")
    public List<RunningQuery> longRunningQueries() {
        return statsEngine.status.getLongRunningQueryList();
    }

    @GET
    @Timed
    @Path("missing-indexes")
    public List<MissingIndex> missingIndexes() {
        return statsEngine.status.getMissingIndices();
    }

    @GET
    @Timed
    @Path("slow-queries")
    public  List<QueryStat> slowQueries() {
        return statsEngine.status.getSlowQueries();
    }

    @GET
    @Timed
    @Path("query-stats")
    public List<QueryStat> allStats() {
        return statsEngine.status.getAllQueryStats();
    }

    @GET
    @Timed
    @Path("index-usages")
    public List<IndexUsage> indexUsages() {
        return statsEngine.status.getIndexUsage();
    }

    @GET
    @Timed
    @Path("relation-sizes")
    public Map<String, List<RelationSize>> relationSizes() {
        Map<String, List<RelationSize>> results = new HashMap<>();
        results.put("index", new LinkedList<>());
        results.put("table", new LinkedList<>());

        for(RelationSize r : statsEngine.status.getRelationSizes()) {
            results.get(r.type).add(r);
        }

        return results;
    }

    @GET
    @Timed
    @Path("unused-indexes")
    public List<UnusedIndex> unusedIndexes() {
        return statsEngine.status.getUnusedIndices();
    }

    @GET
    @Timed
    @Path("unused-tables")
    public List<UnusedTable> unusedTables() {
        return statsEngine.status.getUnusedTables();
    }

    @GET
    @Timed
    @Path("index-hit-rate")
    public Double indexHitRate() {
        return statsEngine.status.getIndexHitRate();
    }

    @GET
    @Timed
    @Path("table-hit-rate")
    public Double tableHitRate() {
        return statsEngine.status.getTableHitRate();
    }


    @GET
    @Timed
    @Path("db-stats-average-query-time")
    public String averageQueryTime() {
        try {
            return mapper.writeValueAsString(
                    statsEngine.dbStats.averageQueryTime.downSample(Frequency.of(30, TimeUnit.SECONDS), SampleMethod.MEAN)
            );
        } catch (JsonProcessingException e) {
            return "{ }";
        }
    }

    @GET
    @Timed
    @Path("db-stats-concurrent-queries")
    public String concurrentQueries() {
        try {
            return mapper.writeValueAsString(
                    statsEngine.dbStats.concurrentQueries.downSample(Frequency.of(30, TimeUnit.SECONDS), SampleMethod.SUM)
            );
        } catch (JsonProcessingException e) {
            return "{ }";
        }
    }

    @GET
    @Timed
    @Path("db-stats-open-connections")
    public String openConnections() {
        try {
            return mapper.writeValueAsString(
                    statsEngine.dbStats.connectionCount.downSample(Frequency.of(30, TimeUnit.SECONDS), SampleMethod.MEAN)
            );
        } catch (JsonProcessingException e) {
            return "{ }";
        }
    }

    @GET
    @Timed
    @Path("query-stats/{hash:[a-fA-F0-9]+}")
    public Response queryStats(@PathParam("hash") String queryHash,
                               @QueryParam("start") Optional<String> startTime,
                               @QueryParam("end") Optional<String> endTime) {
        Query q = QueryCache.QUERIES.getByHash(queryHash);
        if (q != null) {
            DateTime start = DateTime.now().minusHours(1);
            DateTime end = DateTime.now();

            if(startTime.isPresent()) {
                start = DateTime.parse(startTime.get());
            }

            if(endTime.isPresent()) {
                end = DateTime.parse(endTime.get());
            }

            Frequency frequency = Frequency.of(30, TimeUnit.SECONDS);
            DataFrame<DateTime, BigDecimal> dataFrame = new DataFrame<>();
            dataFrame.add("Average Query Time (ms)",
                    q.averageQueryTimes.downSampleToTimeWindow(start, end, frequency, SampleMethod.MEAN)
            );
            dataFrame.add("Total number of calls",
                    q.callCounts.downSampleToTimeWindow(start, end, frequency, SampleMethod.SUM)
            );
            dataFrame.add("Total Time Spent (ms)",
                   q.totalTimes.downSampleToTimeWindow(start, end, frequency, SampleMethod.SUM)
            );

            try {
                StringWriter writer = new StringWriter();
                JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                serializeDataFrame(dataFrame, jsonGenerator);
                writer.close();
                return Response.ok(writer.toString()).build();
            } catch (IOException e) {
                e.printStackTrace();
                return Response.serverError().build();
            }
        } else {
            return Response.status(404).build();
        }
    }

    @GET
    @Timed
    @Path("db-stats")
    public String dbstats() {
        Frequency frequency = Frequency.of(30, TimeUnit.SECONDS);
        DateTime start = DateTime.now().minusHours(1);
        DateTime end = DateTime.now();

        try {
            DataFrame<DateTime, BigDecimal> dataFrame = statsEngine.dbStats.toDataFrame(start, end, frequency);
            StringWriter writer = new StringWriter();
            JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
            serializeDataFrame(dataFrame, jsonGenerator);
            writer.close();
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "{ }";
        }
    }

    private void serializeDataFrame(DataFrame<DateTime, BigDecimal> df, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject(); {

            jsonGenerator.writeArrayFieldStart("index");
            for(DateTime dt : df.getIndex()) {
                jsonGenerator.writeNumber(dt.getMillis());
            }
            jsonGenerator.writeEndArray();

            for(String k : df.getValuesMap().keySet()) {
                jsonGenerator.writeArrayFieldStart(k);
                Map<DateTime, BigDecimal> valueMap = df.getValuesMap().get(k);
                for(DateTime dt : df.getIndex()) {
                    jsonGenerator.writeNumber(valueMap.get(dt));
                }
                jsonGenerator.writeEndArray();
            }

        }  jsonGenerator.writeEndObject();

        jsonGenerator.close();
    }

}
