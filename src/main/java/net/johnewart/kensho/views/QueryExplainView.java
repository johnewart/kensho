package net.johnewart.kensho.views;

import io.dropwizard.views.View;
import net.johnewart.kensho.core.Query;

public class QueryExplainView extends View {
    private final Query query;

    public QueryExplainView(Query q) {
        super("/views/ftl/query_explain.ftl");
        this.query = q;
    }
}
