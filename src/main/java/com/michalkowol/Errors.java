package com.michalkowol;

import lombok.Value;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public final class Errors {

    private Errors() {
    }

    interface ServerError {
        int getStatus();
        String getCode();
        default UUID getId() {
            return UUID.randomUUID();
        }
        String getMessage();
    }

    @Value
    static class NotFound implements ServerError {
        private final int status = HTTP_NOT_FOUND;
        private final String code = "NF";
        private final String message;
    }

    @Value
    static class InternalServerError implements ServerError {
        private final int status = HTTP_INTERNAL_ERROR;
        private final String code = "IE";
        private final String message;
        private final String stackTrace;

        static InternalServerError create(Exception ex) {
            return new InternalServerError(ex.getMessage(), extractStackTrace(ex));
        }
    }

    @Value
    static class BadRequest implements ServerError {
        private final int status = HTTP_BAD_REQUEST;
        private final String code = "BR";
        private final String message;
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
        public NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
        public BadRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static String extractStackTrace(Throwable throwable) {
        StringWriter errorMsgWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errorMsgWriter));
        return errorMsgWriter.toString();
    }
}
