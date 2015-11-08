package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import org.sixtysecond.dashboard.jenkins.JenkinsQueryResponse;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/jenkins-jobs")
@Produces(MediaType.APPLICATION_JSON)
public class JenkinsQueryResource {

    private final Client client;
    private final AtomicLong counter;

    public JenkinsQueryResource(Client client) {
        this.client = client;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public JenkinsQueryResponse queryJenkins(@QueryParam("jenkinsServerUrl") Optional<String> jenkinsServerUrl,

                               @QueryParam("jenkinsJobPattern") Optional<String> jenkinsJobPattern) {

        //TODO: implement
        return new JenkinsQueryResponse();
    }
}