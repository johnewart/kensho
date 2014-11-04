package net.johnewart.kensho.core;

import net.johnewart.shuzai.TimeSeries;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Query {
    public final String query;
    public final String hash;
    public final TimeSeries averageQueryTimes;
    public final TimeSeries totalTimes;
    public final TimeSeries callCounts;

    public Query(String query) {
        this.query = query;
        this.hash = hashQuery(query);
        this.averageQueryTimes = new TimeSeries();
        this.totalTimes = new TimeSeries();
        this.callCounts = new TimeSeries();
    }

    public void addStat(QueryStat stat) {
        DateTime now = DateTime.now();
        this.averageQueryTimes.add(now, new BigDecimal(stat.averageTime));
        this.totalTimes.add(now, new BigDecimal(stat.totalTime));
        this.callCounts.add(now, new BigDecimal(stat.numberOfCalls));
    }

    public String getQuery() {
        return query;
    }

    public String getHash() {
        return hash;
    }

    public BigDecimal getTotalCallCount() {
        return callCounts.sum();
    }

    public BigDecimal getAverageTime() {
        return averageQueryTimes.mean();
    }

    public BigDecimal getTotalQueryTime() {
        return totalTimes.sum();
    }

    public static String computeHash(String queryString) {
        return hashQuery(queryString);
    }

    /**
     * Compute the MD5 hash of the query by first up-casing the query string
     * @param query The full query string
     * @return A hexadecimal MD5 hash string
     */
    private static String hashQuery(String query) {
        try {
            byte[] bytesOfMessage = query.toUpperCase().getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            return (new HexBinaryAdapter()).marshal(thedigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
