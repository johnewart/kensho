package net.johnewart.kensho.core;

import com.google.common.util.concurrent.AtomicDouble;
import com.jolbox.bonecp.BoneCP;
import net.johnewart.kensho.sql.DataAccessException;
import net.johnewart.kensho.sql.PgStatusQueries;
import net.johnewart.kensho.sql.ResultSetProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static net.johnewart.kensho.sql.SQL.select;

public class PgStatus {
    private final BoneCP connectionPool;
    private static Logger LOG = LoggerFactory.getLogger(PgStatus.class);

    public PgStatus(BoneCP connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<RunningQuery> getRunningQueryList() {
        return getRunningQueries(false);
    }

    public List<RunningQuery> getLongRunningQueryList() {
        return getRunningQueries(true);
    }

    private List<RunningQuery> getRunningQueries(boolean longRunning) {
        final String query;
        if(longRunning) {
            query = PgStatusQueries.LONG_RUNNING_QUERIES;
        } else {
            query = PgStatusQueries.RUNNING_QUERIES;
        }

        List<RunningQuery> runningQueries = new LinkedList<>();
        selectWithConnection(query, (rs, cnt)-> {
            RunningQuery rq = new RunningQuery();
            rq.duration = rs.getString("duration");
            rq.waiting = rs.getString("waiting");
            rq.query = rs.getString("query");
            rq.state = rs.getString("state");
            rq.source = rs.getString("source");
            rq.pid = rs.getInt("pid");
            rq.startedAt = rs.getString("started_at");
            runningQueries.add(rq);
        });
        return runningQueries;
    }

    public List<MissingIndex> getMissingIndices() {
        List<MissingIndex> missingIndices = new LinkedList<>();
        selectWithConnection(PgStatusQueries.MISSING_INDEXES,(rs, cnt)-> {
            MissingIndex mi = new MissingIndex();
            mi.tableName = rs.getString("table");
            mi.percentOfTimesIndexUsed = rs.getInt("percent_of_times_index_used");
            mi.rowsInTable = rs.getLong("rows_in_table");
            missingIndices.add(mi);
        });
        return missingIndices;
    }

    public List<QueryStat> getSlowQueries() {
        return getQueryStats(true);
    }

    public List<QueryStat> getAllQueryStats() {
        return getQueryStats(false);
    }

    private List<QueryStat> getQueryStats(boolean slow) {
        final String query;
        if (slow) {
            query = PgStatusQueries.SLOW_QUERIES;
        } else {
            query = PgStatusQueries.QUERY_STATS;
        }

        List<QueryStat> queryStats = new LinkedList<>();
        selectWithConnection(query,(rs, cnt)-> {
            QueryStat sq = new QueryStat();
            sq.query = rs.getString("query");
            sq.totalTime = rs.getLong("total_time");
            sq.averageTime = rs.getFloat("average_time");
            sq.numberOfCalls = rs.getLong("calls");
            queryStats.add(sq);
        });
        return queryStats;
    }

    public List<RelationSize>  getRelationSizes() {
        List<RelationSize> relationSizes = new LinkedList<>();
        selectWithConnection(PgStatusQueries.RELATION_SIZES,(rs, cnt)-> {
            RelationSize size = new RelationSize();
            size.name = rs.getString("name");
            size.type = rs.getString("type");
            size.size = rs.getLong("size");
            relationSizes.add(size);
        });
        return relationSizes;
    }

    public List<UnusedIndex> getUnusedIndices() {
        List<UnusedIndex> unusedIndexes = new LinkedList<>();
        selectWithConnection(PgStatusQueries.UNUSED_INDEXES,(rs, cnt)-> {
            UnusedIndex size = new UnusedIndex();
            size.table = rs.getString("table");
            size.index = rs.getString("index");
            size.indexSize = rs.getLong("index_size");
            size.indexScans = rs.getString("index_scans");
            unusedIndexes.add(size);
        });
        return unusedIndexes;
    }

    public List<UnusedTable> getUnusedTables() {
        List<UnusedTable> unusedTables = new LinkedList<>();
        selectWithConnection(PgStatusQueries.UNUSED_TABLES,(rs, cnt)-> {
            UnusedTable size = new UnusedTable();
            size.table = rs.getString("table");
            size.rowsInTable = rs.getLong("rows_in_table");
            unusedTables.add(size);
        });
        return unusedTables;
    }


    public List<IndexUsage> getIndexUsage() {
        List<IndexUsage> indexUsages = new LinkedList<>();
        selectWithConnection(PgStatusQueries.INDEX_USAGE,(rs, cnt)-> {
            IndexUsage usage = new IndexUsage();
            usage.table = rs.getString("table");
            usage.percentageOfQueriesUsed = rs.getLong("percent_of_times_index_used");
            usage.rowsInTable = rs.getLong("rows_in_table");
            indexUsages.add(usage);
        });
        return indexUsages;
    }

    public Double getIndexHitRate() {
        final AtomicDouble hitRate = new AtomicDouble();
        selectWithConnection(PgStatusQueries.INDEX_HIT_RATE,(rs, cnt)-> {
            hitRate.set(rs.getDouble("rate"));
        });
        return hitRate.get();
    }

    public Double getTableHitRate() {
        final AtomicDouble hitRate = new AtomicDouble();
        selectWithConnection(PgStatusQueries.TABLE_HIT_RATE,(rs, cnt)-> {
            hitRate.set(rs.getDouble("rate"));
        });
        return hitRate.get();
    }

    public Long getCurrentConnectionCount() {
        final AtomicLong connectionCount = new AtomicLong();
        selectWithConnection(PgStatusQueries.CONNECTION_COUNT,(rs, cnt)-> {
            connectionCount.set(rs.getLong("connections"));
        });
        return connectionCount.get();
    }

    public List<Lock> getLocks() {
        List<Lock> locks = new LinkedList<>();
        selectWithConnection(PgStatusQueries.LOCKS,(rs, cnt)-> {
            Lock lock = new Lock();
            lock.pid = rs.getInt("pid");
            lock.query = rs.getString("query");
            lock.age = rs.getString("age");
            locks.add(lock);
        });
        return locks;
    }

    public Long getTransactionCount() {
        final AtomicLong transactionCount = new AtomicLong();
        selectWithConnection(PgStatusQueries.TRANSACTION_COUNT,(rs, cnt)-> {
            transactionCount.set(rs.getLong("transactions"));
        });
        return transactionCount.get();
    }


    public Long getDatabaseSize() {
        final AtomicLong databaseSize = new AtomicLong();
        selectWithConnection(PgStatusQueries.DATABASE_SIZE,(rs, cnt)-> {
            databaseSize.set(rs.getLong("database_size"));
        });
        return databaseSize.get();
    }

    public void resetStats() {
        selectWithConnection(PgStatusQueries.RESET_QUERY_STATS, null);
    }

    private void selectWithConnection(String sql,
                                      ResultSetProcessor processor,
                                      Object... params) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            if(conn != null)
            {
                select(conn, sql, processor, params);
            }
        } catch (SQLException se) {
            LOG.error("SQL Error: " , se);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn != null)
                    conn.close();

            } catch (SQLException innerEx) {
                LOG.debug("Error cleaning up: " + innerEx);
            }
        }
    }
}
