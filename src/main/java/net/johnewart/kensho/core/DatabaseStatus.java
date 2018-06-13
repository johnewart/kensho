package net.johnewart.kensho.core;

import java.util.List;

/**
 * Created by jewart on 2/17/15.
 */
public interface DatabaseStatus {
    List<RunningQuery> getRunningQueryList();

    List<RunningQuery> getLongRunningQueryList();

    List<MissingIndex> getMissingIndices();

    List<QueryStat> getSlowQueries();

    List<QueryStat> getAllQueryStats();

    List<RelationSize>  getRelationSizes();

    List<UnusedIndex> getUnusedIndices();

    List<UnusedTable> getUnusedTables();

    List<IndexUsage> getIndexUsage();

    Double getIndexHitRate();

    Double getTableHitRate();

    Long getCurrentConnectionCount();

    List<Lock> getLocks();

    Long getTransactionCount();

    Long getDatabaseSize();

    void resetStats();
}
