package net.johnewart.kensho.resources;

import io.dropwizard.views.View;
import net.johnewart.kensho.core.Query;
import net.johnewart.kensho.stats.QueryCache;
import net.johnewart.kensho.views.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/query/{hash:[a-fA-F0-9]+}")
@Produces(MediaType.TEXT_HTML)
public class QueryResource {

    public QueryResource() {
    }

    @GET
    public View queryStats(@PathParam("hash") String queryHash) {
        Query q = QueryCache.QUERIES.getByHash(queryHash);
        return new QueryView(q);

    }

    @GET
    @Path("explain")
    public View explain(@PathParam("hash") String queryHash) {
        Query q = QueryCache.QUERIES.getByHash(queryHash);
        return new QueryExplainView(q);
    }

}
