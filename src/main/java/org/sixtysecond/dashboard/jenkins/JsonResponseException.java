package org.sixtysecond.dashboard.jenkins;

/**
 * Created by edriggs on 11/8/15.
 */
public class JsonResponseException extends Exception {
    private LastBuildResponse lastBuildResponse;

    public LastBuildResponse getLastBuildResponse() {
        return lastBuildResponse;
    }

    public JsonResponseException setLastBuildResponse(LastBuildResponse lastBuildResponse) {
        this.lastBuildResponse = lastBuildResponse;
        return this;
    }
}
