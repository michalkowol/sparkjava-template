package com.michalkowol;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.flywaydb.core.Flyway;
import org.h2.tools.Server;

import static com.michalkowol.Modules.*;

public class Boot {

    public static void main(String[] args) throws Throwable {
        Injector injector = configure();
        Server h2DatabaseServer = injector.getInstance(Server.class);
        Flyway flyway = injector.getInstance(Flyway.class);
        HttpServer httpServer = injector.getInstance(HttpServer.class);

        h2DatabaseServer.start();
        flyway.migrate();
        httpServer.start();
    }

    private static Injector configure() {
        return Guice.createInjector(
            new ConfigModule(),
            new HttpClientModule(),
            new HttpServerModule(),
            new JsonXmlModule(),
            new DatabaseModule()
        );
    }
}
