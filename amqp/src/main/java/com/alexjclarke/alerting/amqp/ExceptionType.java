package com.alexjclarke.alerting.amqp;

public enum ExceptionType {
    INVALID_REQUEST_EXCEPTION(400),
    INVALID_ACCOUNT_EXCEPTION(403),
    NOT_FOUND_EXCEPTION(404),
    REQUEST_TIMEOUT_EXCEPTION(408),
    CONFLICT_EXCEPTION(409),
    UNPROCESSABLE_ENTITY_EXCEPTION(422),
    INTERNAL_SERVER_EXCEPTION(500);

    private final int status;

    ExceptionType(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
