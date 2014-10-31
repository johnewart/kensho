package net.johnewart.kensho.views;

import io.dropwizard.views.View;
import net.johnewart.kensho.core.Query;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

public class QueryView extends View {
    private final Query query;
    public QueryView(Query query) {
        super("/views/ftl/query.ftl");
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    public String getPrettySQL() {
        String formattedSQL = new BasicFormatterImpl().format(query.query);
        return formattedSQL;
    }


}
