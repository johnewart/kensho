package net.johnewart.kensho;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import net.johnewart.kensho.core.PgStatus;
import net.johnewart.kensho.resources.DataResource;
import net.johnewart.kensho.resources.DashboardResource;
import net.johnewart.kensho.resources.QueryResource;
import net.johnewart.kensho.stats.StatsEngine;

import java.sql.SQLException;

public class KenshoApplication extends Application<KenshoConfiguration> {
    public static void main(String[] args) throws Exception {
        new KenshoApplication().run(args);
    }

    @Override
    public String getName() {
        return "kensho";
    }

    @Override
    public void initialize(Bootstrap<KenshoConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(KenshoConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {

        final BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl(configuration.getDataSourceFactory().getUrl());
        config.setUsername(configuration.getDataSourceFactory().getUser());
        config.setPassword(configuration.getDataSourceFactory().getPassword());
        config.setMinConnectionsPerPartition(1);
        config.setMaxConnectionsPerPartition(2);
        config.setPartitionCount(1);

        try {
            final BoneCP connectionPool = new BoneCP(config);
            final PgStatus status = new PgStatus(connectionPool);
            final StatsEngine statsEngine = new StatsEngine(status);

            environment.jersey().register(new DashboardResource(statsEngine));
            environment.jersey().register(new DataResource(statsEngine));
            environment.jersey().register(new QueryResource());

        } catch (SQLException e) {
            System.exit(0);
            e.printStackTrace();
        }
    }


}
