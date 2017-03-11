package com.michalkowol;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.softwareberg.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.flywaydb.core.Flyway;
import org.h2.tools.Server;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.File;
import java.util.Optional;

final class Modules {

    private Modules() {
    }

    static class HttpClientModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Singleton
        @Provides
        private static AsyncHttpClient provideAsyncHttpClient() {
            return new DefaultAsyncHttpClient();
        }

        @Singleton
        @Provides
        private static HttpClient provideHttpClient(AsyncHttpClient asyncHttpClient) {
            return new SimpleHttpClient(asyncHttpClient);
        }
    }

    static class JsonXmlModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Singleton
        @Provides
        private static JsonMapper provideJsonMapper() {
            return JsonMapper.Companion.create();
        }

        @Singleton
        @Provides
        private static XmlMapper provideXmlMapper() {
            return XmlMapper.Companion.create();
        }
    }

    static class DatabaseModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Singleton
        @Provides
        @SneakyThrows
        static DataSource provideDataSource(Config config) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getString("datasource.jdbcUrl"));
            hikariConfig.setUsername(config.getString("datasource.username"));
            hikariConfig.setPassword(config.getString("datasource.password"));
            return new HikariDataSource(hikariConfig);
        }

        @Singleton
        @Provides
        private static Database provideDatabase(DataSource dataSource) {
            return new Database(dataSource);
        }

        @Singleton
        @Provides
        private static Flyway provideFlyway(DataSource dataSource) {
            Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            return flyway;
        }

        @Singleton
        @Provides
        @SneakyThrows
        private static Server provideH2Database() {
            return Server.createTcpServer();
        }
    }

    static class ConfigModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Singleton
        @Provides
        private static Config provideConfig() {
            Optional<String> stage = Optional.ofNullable(System.getProperty("environment"));
            String configurationFile = stage
                .map(status -> "application-" + status + ".properties")
                .orElse("application.conf");

            return ConfigFactory
                .parseFile(new File("application.conf"))
                .withFallback(ConfigFactory.load(configurationFile))
                .withFallback(ConfigFactory.load());
        }
    }

    static class HttpServerModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Singleton
        @Provides
        private static ServerConfiguration provideServerConfiguration(Config config) {
            int port = config.getInt("server.port");
            return new ServerConfiguration(port);
        }
    }
}
