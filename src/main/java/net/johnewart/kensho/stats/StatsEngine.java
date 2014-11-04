package net.johnewart.kensho.stats;

import net.johnewart.kensho.core.PgStatus;
import net.johnewart.kensho.core.Query;
import net.johnewart.kensho.core.QueryStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatsEngine {
    final ScheduledExecutorService scheduler;
    public final DbStats dbStats;
    public final PgStatus status;
    private static final Logger LOG = LoggerFactory.getLogger(StatsEngine.class);

    public StatsEngine(PgStatus status) {
        this.status = status;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.dbStats = new DbStats();

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            LOG.debug("Taking a snapshot of queries!");
            for(QueryStat stat : status.getAllQueryStats()) {
                Query q = QueryCache.QUERIES.getOrPutQuery(stat.query);
                q.addStat(stat);
            }

            for(QueryStat stat : status.getSlowQueries()) {
                Query q = QueryCache.SLOW_QUERIES.getOrPutQuery(stat.query);
                q.addStat(stat);
            }

            LOG.debug("Snapshotting DB-level data");
            dbStats.snapshot(status);
            LOG.debug("Resetting DB stats");
            status.resetStats();
            }
        }, 1, 2, TimeUnit.SECONDS);
    }


}
