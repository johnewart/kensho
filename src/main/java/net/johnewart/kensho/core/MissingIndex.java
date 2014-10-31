package net.johnewart.kensho.core;

public class MissingIndex {
    public String tableName;
    public Integer percentOfTimesIndexUsed;
    public Long rowsInTable;

    public String getTableName() {
        return tableName;
    }

    public Integer getPercentOfTimesIndexUsed() {
        return percentOfTimesIndexUsed;
    }

    public Long getRowsInTable() {
        return rowsInTable;
    }
}
