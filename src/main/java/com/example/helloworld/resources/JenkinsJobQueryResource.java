package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQuery;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQueryCallable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Path("/jenkins-job")
public class JenkinsJobQueryResource {


    private final AtomicLong counter;

    public JenkinsJobQueryResource() {

        this.counter = new AtomicLong();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public JSONObject queryJenkins(@QueryParam("jenkinsServerUrl") Optional<String> jenkinsServerUrl,

                                   @QueryParam("jenkinsJobPattern") Optional<String> jenkinsJobPattern) {

        return new JenkinsJobQueryCallable(
                new JenkinsJobQuery()
                        .setJenkinsServerUrl(jenkinsServerUrl.get())
                        .setJobNamePattern(jenkinsJobPattern.get())
        ).call();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed

    public Response queryJenkins(List<JenkinsJobQuery> jenkinsJobQueryList) {

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < jenkinsJobQueryList.size(); i++) {
            JenkinsJobQuery jenkinsJobQuery = jenkinsJobQueryList.get(i);
            jsonArray.put(i, new JenkinsJobQueryCallable(jenkinsJobQuery).call());
        }
        return Response.status(Response.Status.OK)
                .entity(jsonArray)
                .build();
    }
}