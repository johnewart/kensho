package net.johnewart.kensho.core;


public class IndexUsage {
    public String table;
    public Long percentageOfQueriesUsed;
    public Long rowsInTable;

    public String getTable() {
        return table;
    }

    public Long getPercentageOfQueriesUsed() {
        return percentageOfQueriesUsed;
    }

    public Long getRowsInTable() {
        return rowsInTable;
    }
}
