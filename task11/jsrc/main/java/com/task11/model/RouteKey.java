package com.task11.model;


import java.util.Objects;

public class RouteKey {
    private String path;
    private String method;

    public RouteKey(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteKey routeKey = (RouteKey) o;
        return Objects.equals(path, routeKey.path) && Objects.equals(method, routeKey.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method);
    }
}