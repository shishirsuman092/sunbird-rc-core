package io.opensaber.registry.sink;

import io.opensaber.registry.middleware.util.Constants;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.umlg.sqlg.structure.SqlgGraph;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class SqlgProvider implements DatabaseProvider {

    private Logger logger = LoggerFactory.getLogger(SqlgProvider.class);
    private Environment environment;
    private Graph graph;

    public SqlgProvider(Environment environment) {
        this.environment = environment;
        String jdbcUrl = environment.getProperty("jdbc.url");
        String jdbcUsername = environment.getProperty("jdbc.username");
        String jdbcPassword = environment.getProperty("jdbc.password");
        Configuration config = new BaseConfiguration();
        config.setProperty("jdbc.url", jdbcUrl);
        config.setProperty("jdbc.username", jdbcUsername);
        config.setProperty("jdbc.password", jdbcPassword);
        graph = SqlgGraph.open(config);
    }

    @Override
    public Graph getGraphStore() {
        return graph;
    }

    @PostConstruct
    public void init() {
        logger.info("**************************************************************************");
        logger.info("Initializing SQLG DB instance ...");
        logger.info("**************************************************************************");
    }

    @PreDestroy
    public void destroy() throws Exception {
        logger.info("**************************************************************************");
        logger.info("Gracefully shutting down SQLG DB instance ...");
        logger.info("**************************************************************************");
        graph.close();
    }
}
