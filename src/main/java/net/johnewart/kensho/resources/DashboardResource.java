package net.johnewart.kensho.resources;

import io.dropwizard.views.View;
import net.johnewart.kensho.stats.StatsEngine;
import net.johnewart.kensho.views.DashboardView;
import net.johnewart.kensho.views.IndexView;
import net.johnewart.kensho.views.QueriesView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/dashboard")
@Produces(MediaType.TEXT_HTML)
public class DashboardResource {

    private final StatsEngine statsEngine;

    public DashboardResource(StatsEngine statsEngine) {
        this.statsEngine = statsEngine;
    }

    @GET
    public View home() {
        return new DashboardView(statsEngine);
    }

    @GET
    @Path("queries")
    public View queries() {
        return new QueriesView(statsEngine);
    }

    @GET
    @Path("indexes")
    public View indexes() {
        return new IndexView(statsEngine);
    }

}
