package org.sixtysecond.jenkins.core;

import java.util.concurrent.Callable;

public class JenkinsQuery implements Callable {
    private final long id;
    private final String jenkinsServerUrl;
    private final String jobNamePattern;

    public JenkinsQuery(long id, String jenkinsServerUrl, String jobNamePattern) {
        this.id = id;
        this.jenkinsServerUrl = jenkinsServerUrl;
        this.jobNamePattern = jobNamePattern;
    }

    public long getId() {
        return id;
    }

    public String getJenkinsServerUrl() {
        return jenkinsServerUrl;
    }

    public String getJobNamePattern() {
        return jobNamePattern;
    }

    public JenkinsQueryResponse call() {
        JenkinsQueryResponse jenkinsQueryResponse = new JenkinsQueryResponse();
        //TODO: implement
        return jenkinsQueryResponse;

    }
}
