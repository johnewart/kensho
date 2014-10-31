package net.johnewart.kensho.stats;

import net.johnewart.kensho.core.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryMap {
    private final Map<String, Query> queryMap;

    public QueryMap() {
        queryMap = new ConcurrentHashMap<>();
    }

    public Query getOrPutQuery(String queryString) {
        String queryHash = Query.computeHash(queryString);
        if(!queryMap.containsKey(queryHash)) {
            queryMap.put(queryHash, new Query(queryString));
        }
        return queryMap.get(queryHash);
    }

    public  List<Query> getAllQueries() {
        return new LinkedList<>(queryMap.values());
    }

    public Query getByHash(String queryHash) {
        return queryMap.get(queryHash);
    }
}
