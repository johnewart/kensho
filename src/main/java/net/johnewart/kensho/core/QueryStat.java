package net.johnewart.kensho.core;

import java.util.concurrent.TimeUnit;

public class QueryStat {
    public String query;
    public Long totalTime;
    public Float averageTime;
    public Long numberOfCalls;

    public String getQuery() {
        return query;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public Float getAverageTime() {
        return averageTime;
    }

    public Long getNumberOfCalls() {
        return numberOfCalls;
    }

    public String getFormattedTotalTime() {
        long millis = getTotalTime().longValue();
        if (millis > 1000) {
            return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else {
            return String.format("%d msec", millis);
        }
    }

    public String getFormattedAverageTime() {
        Float millis = getAverageTime();
        if (millis > 1000) {
            return String.format("%d sec",
                    TimeUnit.MILLISECONDS.toSeconds(millis.longValue())
            );
        } else {
            return String.format("%.2f msec", millis);
        }
    }
}
