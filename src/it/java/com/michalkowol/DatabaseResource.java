package com.michalkowol;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.h2.tools.Server;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;

public abstract class DatabaseResource {


    public static class H2DatabaseResource extends ExternalResource {

        private Server server;

        @Override
        protected void before() throws Throwable {
            super.before();
            server = Server.createTcpServer();
        }

        @Override
        protected void after() {
            super.after();
            server.shutdown();
        }
    }

    public static class DataSourceResource extends ExternalResource {

        private HikariDataSource dataSource;

        @Override
        protected void before() throws Throwable {
            super.before();
            dataSource = createLocalDataSource();
        }

        @Override
        protected void after() {
            super.after();
            dataSource.close();
        }

        private HikariDataSource createLocalDataSource() {
            Config config = ConfigFactory.load();
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getString("datasource.jdbcUrl"));
            hikariConfig.setUsername(config.getString("datasource.username"));
            hikariConfig.setPassword(config.getString("datasource.password"));
            return new HikariDataSource(hikariConfig);
        }

        public void cleanAndMigrateDatabase() {
            Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            flyway.clean();
            flyway.migrate();
        }

        public void prepareDatabase(Operation operation) {
            new DbSetup(new DataSourceDestination(dataSource), operation).launch();
        }

        public DataSource getDataSource() {
            return dataSource;
        }
    }
}
