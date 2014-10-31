package net.johnewart.kensho.views;

import io.dropwizard.views.View;
import net.johnewart.kensho.core.Query;
import net.johnewart.kensho.stats.QueryCache;
import net.johnewart.kensho.stats.StatsEngine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QueriesView extends View {
    private final StatsEngine stats;

    public QueriesView(StatsEngine statsEngine) {
        super("/views/ftl/queries.ftl");
        stats = statsEngine;
    }

    /**
     * Get all queries, reverse-sorted by total time
     * @return List<Query> all query objects
     */
    public List<Query> getAllQueries() {
        List<Query> queries = QueryCache.QUERIES.getAllQueries();
        Collections.sort(queries, new Comparator<Query>() {
            @Override
            public int compare(Query o1, Query o2) {
                return o2.getTotalQueryTime().compareTo(o1.getTotalQueryTime());
            }
        });
        return queries;
    }

    public List<Query> getSlowQueries() {
        List<Query> queries =  QueryCache.SLOW_QUERIES.getAllQueries();
        Collections.sort(queries, new Comparator<Query>() {
            @Override
            public int compare(Query o1, Query o2) {
                return o2.getTotalQueryTime().compareTo(o1.getTotalQueryTime());
            }
        });
        return queries;
    }

}
