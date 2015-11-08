package org.sixtysecond.dashboard.jenkins;

/**
 * Created by edriggs on 11/2/15.
 */
public class JenkinsJob {

    private final String description;
    private final String name;
    private final String url;

    public JenkinsJob(String description, String name, String url) {
        this.description = description;
        this.name = name;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
