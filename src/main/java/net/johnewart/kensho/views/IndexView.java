package net.johnewart.kensho.views;

import io.dropwizard.views.View;
import net.johnewart.kensho.core.IndexUsage;
import net.johnewart.kensho.core.MissingIndex;
import net.johnewart.kensho.core.QueryStat;
import net.johnewart.kensho.stats.StatsEngine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndexView extends View {
    private final StatsEngine stats;

    public IndexView(StatsEngine statsEngine) {
        super("/views/ftl/indexes.ftl");
        stats = statsEngine;
    }

    public List<IndexUsage> getAllIndexes() {
        List<IndexUsage> indexUsage = stats.status.getIndexUsage();

        Collections.sort(indexUsage, new Comparator<IndexUsage>() {
            @Override
            public int compare(IndexUsage o1, IndexUsage o2) {
                int offset = 0;
                if (o1.rowsInTable > o2.rowsInTable)
                    offset -= 1;
                if (o1.percentageOfQueriesUsed < o2.percentageOfQueriesUsed)
                    offset -= -1;
                if (o1.percentageOfQueriesUsed > o2.percentageOfQueriesUsed)
                    offset += 1;
                else
                    offset += 0;

                return offset;
            }
        });

        return indexUsage;
    }

    public List<QueryStat> getSlowQueries() {
        return stats.status.getSlowQueries();
    }

    public List<MissingIndex> getMissingIndexes() {
        return stats.status.getMissingIndices();
    }

}
