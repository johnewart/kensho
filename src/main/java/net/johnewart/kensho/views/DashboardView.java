package net.johnewart.kensho.views;

import io.dropwizard.views.View;
import net.johnewart.kensho.core.QueryStat;
import net.johnewart.kensho.stats.StatsEngine;

import java.util.List;

public class DashboardView extends View {
    private final StatsEngine statsEngine;

    public DashboardView(StatsEngine statsEngine) {
        super("/views/ftl/index.ftl");
        this.statsEngine = statsEngine;
    }

    public List<QueryStat> getSlowQueries() {
        return statsEngine.status.getSlowQueries();
    }
}
