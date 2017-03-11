package com.michalkowol;

import com.google.common.collect.ImmutableMap;
import com.softwareberg.JsonMapper;
import com.michalkowol.Errors.NotFoundException;
import com.michalkowol.cars.CarsController;
import com.michalkowol.hackernews.HackerNewsController;
import lombok.Value;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Random;

import static spark.Spark.*;

@Value
class ServerConfiguration {
    private final int port;
}

@Singleton
class HttpServer {

    private final ServerConfiguration serverConfiguration;
    private final JsonMapper jsonMapper;
    private final ErrorsController errorsController;
    private final HackerNewsController hackerNewsController;
    private final CarsController carsController;

    @Inject
    HttpServer(ServerConfiguration serverConfiguration, JsonMapper jsonMapper, ErrorsController errorsController, HackerNewsController hackerNewsController, CarsController carsController) {
        this.serverConfiguration = serverConfiguration;
        this.jsonMapper = jsonMapper;
        this.errorsController = errorsController;
        this.hackerNewsController = hackerNewsController;
        this.carsController = carsController;
    }

    void start() {
        port(serverConfiguration.getPort());

        staticFiles.location("/public");
        redirect.get("/redirect", "/health");
        get("/health", this::health);
        get("/errors", this::errors, jsonMapper::write);

        errorsController.start();
        carsController.start();
        hackerNewsController.start();
    }

    private String health(Request request, Response response) {
        response.type("application/json");
        return "{\"health\": \"ok\"}";
    }

    private Map<String, String> errors(Request request, Response response) {
        response.type("application/json");
        int random = new Random().nextInt(3);
        if (random == 0) {
            throw new IllegalStateException("Random error");
        } else if (random == 1) {
            throw new NotFoundException("Not found");
        }
        return ImmutableMap.of("health", "ok");
    }
}
