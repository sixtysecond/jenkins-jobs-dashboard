package com.github.sixtysecond.cicdash.jenkins;

import org.json.JSONObject;

/**
 * Created by edriggs on 11/8/15.
 */
public class LastBuildResponse {
    private int statusCode;
    private String message;
    private JSONObject lastBuild;

    public int getStatusCode() {
        return statusCode;
    }

    public LastBuildResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LastBuildResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public JSONObject getLastBuild() {
        return lastBuild;
    }

    public LastBuildResponse setLastBuild(JSONObject lastBuild) {
        this.lastBuild = lastBuild;
        return this;
    }
}
