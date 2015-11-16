package com.github.sixtysecond.cicdash.jenkins;

import com.fasterxml.jackson.annotation.*;

public class JenkinsJobQuery {

    private String jenkinsServerUrl;
    private String jobNamePattern;

    @JsonCreator
    public JenkinsJobQuery() {

    }

    public JenkinsJobQuery(String jenkinsServerUrl,
                           String jobNamePattern) {
        this.jenkinsServerUrl = jenkinsServerUrl;
        this.jobNamePattern = jobNamePattern;

    }

    @JsonGetter("jenkinsServerUrl")
    public String getJenkinsServerUrl() {
        return jenkinsServerUrl;
    }

    @JsonSetter("jenkinsServerUrl")
    public JenkinsJobQuery setJenkinsServerUrl(String jenkinsServerUrl) {
        this.jenkinsServerUrl = jenkinsServerUrl;
        return this;
    }

    @JsonGetter("jobNamePattern")
    public String getJobNamePattern() {
        return jobNamePattern;
    }

    @JsonSetter("jobNamePattern")
    public JenkinsJobQuery setJobNamePattern(String jobNamePattern) {
        this.jobNamePattern = jobNamePattern;
        return this;
    }


    @Override
    public String toString() {
        return "JenkinsJobQuery{" +
                "jenkinsServerUrl='" + jenkinsServerUrl + '\'' +
                ", jobNamePattern='" + jobNamePattern + '\'' +
                '}';
    }
}
