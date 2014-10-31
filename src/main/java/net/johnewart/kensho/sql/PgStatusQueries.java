package net.johnewart.kensho.sql;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PgStatusQueries {
    public static Properties prop;

    static {
        prop = new Properties();
        try {
            InputStream in = PgStatusQueries.class.getResourceAsStream("queries.properties");
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public final static String RUNNING_QUERIES = prop.getProperty("running_queries");
    public final static String LONG_RUNNING_QUERIES = prop.getProperty("long_running_queries");
    public final static String LOCKS = prop.getProperty("locks");
    public final static String INDEX_HIT_RATE = prop.getProperty("index_hit_rate");
    public final static String TABLE_HIT_RATE = prop.getProperty("table_hit_rate");
    public final static String INDEX_USAGE = prop.getProperty("index_usage");
    public final static String MISSING_INDEXES = prop.getProperty("missing_indexes");
    public final static String UNUSED_TABLES = prop.getProperty("unused_tables");
    public final static String UNUSED_INDEXES = prop.getProperty("unused_indexes");
    public final static String RELATION_SIZES = prop.getProperty("relation_sizes");
    public final static String DATABASE_SIZE = prop.getProperty("database_size");
    public final static String KILL = prop.getProperty("kill");
    public final static String KILL_ALL = prop.getProperty("kill_all");
    // http://www.craigkerstiens.com/2013/01/10/more-on-postgres-performance/
    public final static String QUERY_STATS = prop.getProperty("query_stats");
    public final static String SLOW_QUERIES = prop.getProperty("slow_queries");
    public final static String QUERY_STATS_AVAILABLE = prop.getProperty("query_stats_available");
    public final static String QUERY_STATS_ENABLED = prop.getProperty("query_stats_enabled");
    public final static String QUERY_STATS_READABLE = prop.getProperty("query_stats_readable");
    public final static String RESET_QUERY_STATS = prop.getProperty("reset_query_stats");
    public final static String CONNECTION_COUNT = prop.getProperty("connection_count");
    public final static String TRANSACTION_COUNT = prop.getProperty("transaction_count");
}