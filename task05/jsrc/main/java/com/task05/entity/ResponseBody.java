package com.task05.entity;

public class ResponseBody {
    private final int statusCode;
    private final Event event;

    public ResponseBody(int statusCode, Event event) {
        this.statusCode = statusCode;
        this.event = event;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "{" +
                "statusCode=" + statusCode +
                ", event=" + event +
                '}';
    }

}
