package com.michalkowol.hackernews;

import com.softwareberg.JsonMapper;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

import static spark.Spark.get;

@Singleton
public class HackerNewsController {

    private final HackerNewsService hackerNewsService;
    private final JsonMapper jsonMapper;

    @Inject
    HackerNewsController(HackerNewsService hackerNewsService, JsonMapper jsonMapper) {
        this.hackerNewsService = hackerNewsService;
        this.jsonMapper = jsonMapper;
    }

    public void start() {
        get("/news", this::news, jsonMapper::write);
    }

    private HackerNews news(Request request, Response response) {
        response.type("application/json");
        return hackerNewsService.topStory();
    }
}
