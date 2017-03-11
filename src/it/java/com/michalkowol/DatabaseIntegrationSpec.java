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
import org.junit.BeforeClass;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class DatabaseIntegrationSpec {

    protected static HikariDataSource dataSource = createLocalDataSource();

    @BeforeClass
    public static void init() {
        startH2Database();
        cleanAndMigrateDatabase(dataSource);
    }

    private static Server startH2Database() {
        try {
            return Server.createTcpServer().start();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static HikariDataSource createLocalDataSource() {
        Config config = ConfigFactory.load();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getString("datasource.jdbcUrl"));
        hikariConfig.setUsername(config.getString("datasource.username"));
        hikariConfig.setPassword(config.getString("datasource.password"));
        return new HikariDataSource(hikariConfig);
    }

    private static void cleanAndMigrateDatabase(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.clean();
        flyway.migrate();
    }

    protected void prepareDatabase(Operation operation) {
        new DbSetup(new DataSourceDestination(dataSource), operation).launch();
    }
}
