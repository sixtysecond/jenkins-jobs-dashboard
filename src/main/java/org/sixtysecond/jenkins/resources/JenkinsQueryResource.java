package org.sixtysecond.jenkins.resources;

import org.sixtysecond.jenkins.core.JenkinsQuery;
import org.sixtysecond.jenkins.core.JenkinsQueryResponse;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/jenkins-query/")
@Produces(MediaType.APPLICATION_JSON)
public class JenkinsQueryResource {
    private final AtomicLong counter;

    public JenkinsQueryResource() {
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public JenkinsQueryResponse jenkinsQuery(@QueryParam("jenkinsServerUrl") Optional<String> jenkinsServerUrl,
                                             @QueryParam("jobNamePattern") Optional<String> jobNamePattern) {

        JenkinsQuery jenkinsQuery =
                new JenkinsQuery(counter.incrementAndGet(), jenkinsServerUrl.get(), jobNamePattern.get());
        return jenkinsQuery.call();
    }
}