package com.michalkowol;

import com.softwareberg.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.michalkowol.Errors.*;
import static spark.Spark.exception;

@Singleton
@Slf4j
class ErrorsController {

    private final JsonMapper jsonMapper;

    @Inject
    public ErrorsController(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    void start() {
        exception(NotFoundException.class, (ex, request, response) -> {
            log.info(request.url(), ex);
            response.type("application/json");
            NotFound notFound = new NotFound(ex.getMessage());
            response.status(notFound.getStatus());
            response.body(jsonMapper.write(notFound));
        });

        exception(BadRequestException.class, (ex, request, response) -> {
            log.info(request.url(), ex);
            response.type("application/json");
            BadRequest badRequest = new BadRequest(ex.getMessage());
            response.status(badRequest.getStatus());
            response.body(jsonMapper.write(badRequest));
        });

        exception(Exception.class, (ex, request, response) -> {
            log.error(request.url(), ex);
            response.type("application/json");
            InternalServerError internalServerError = InternalServerError.create(ex);
            response.status(internalServerError.getStatus());
            response.body(jsonMapper.write(internalServerError));
        });
    }
}
