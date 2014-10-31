package net.johnewart.kensho.stats;

import net.johnewart.chronos.DataFrame;
import net.johnewart.chronos.Frequency;
import net.johnewart.chronos.SampleMethod;
import net.johnewart.chronos.TimeSeries;
import net.johnewart.kensho.core.PgStatus;
import net.johnewart.kensho.core.QueryStat;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class DbStats {
    private static final Logger LOG = LoggerFactory.getLogger(DbStats.class);

    public final TimeSeries averageQueryTime;
    public final TimeSeries concurrentQueries;
    public final TimeSeries connectionCount;
    public final TimeSeries transactionCount;
    public final TimeSeries databaseSize;
    public final TimeSeries slowQueryCount;

    private BigDecimal lastTxCount;

    public DbStats() {
        this.averageQueryTime = new TimeSeries();
        this.concurrentQueries = new TimeSeries();
        this.connectionCount = new TimeSeries();
        this.transactionCount = new TimeSeries();
        this.databaseSize = new TimeSeries();
        this.slowQueryCount = new TimeSeries();
    }

    public void snapshot(PgStatus status) {
        LOG.debug("DB Stats snapshot!");
        DateTime now = DateTime.now();

        List<QueryStat> queryStatList = status.getAllQueryStats();
        long cc = status.getCurrentConnectionCount();
        long cq = status.getRunningQueryList().size();

        BigDecimal txCount = new BigDecimal(status.getTransactionCount());
        final BigDecimal nextTxCount;
        if (lastTxCount != null) {
            nextTxCount = txCount.subtract(lastTxCount);
        } else {
            // Ignore first reading
            nextTxCount = new BigDecimal(0);
        }
        lastTxCount = txCount;

        float average = 0.0f;
        long count = 0;
        for(QueryStat qs : queryStatList) {
            count += 1;
            average += (qs.averageTime - average) / count;
        }

        averageQueryTime.add(now, new BigDecimal(average));
        concurrentQueries.add(now, new BigDecimal(cq));
        connectionCount.add(now, new BigDecimal(cc));
        transactionCount.add(now, nextTxCount);
        databaseSize.add(now, new BigDecimal(status.getDatabaseSize()));
        slowQueryCount.add(now, new BigDecimal(status.getSlowQueries().size()));
    }

    public DataFrame<DateTime, BigDecimal> toDataFrame(DateTime start, DateTime end, Frequency frequency) {
        DataFrame<DateTime, BigDecimal> dataFrame = new DataFrame<>();

        dataFrame.add("Average Query Time (ms)",
                averageQueryTime.downSampleToTimeWindow(start, end, frequency, SampleMethod.MEAN)
        );
        dataFrame.add("Average Open Connections",
                connectionCount.downSampleToTimeWindow(start, end, frequency, SampleMethod.MEAN)
        );
        dataFrame.add("Concurrent Queries",
                concurrentQueries.downSampleToTimeWindow(start, end, frequency, SampleMethod.SUM)
        );
        dataFrame.add("Transaction Count",
                transactionCount.downSampleToTimeWindow(start, end, frequency, SampleMethod.SUM)
        );
        dataFrame.add("Slow Query Count",
                slowQueryCount.downSampleToTimeWindow(start, end, frequency, SampleMethod.SUM)
        );
        dataFrame.add("Database Size",
                databaseSize.downSampleToTimeWindow(start, end, frequency, SampleMethod.MEAN)
        );


        return dataFrame;
    }


}
