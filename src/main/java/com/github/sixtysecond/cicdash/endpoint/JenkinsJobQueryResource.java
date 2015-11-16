package com.github.sixtysecond.cicdash.endpoint;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import com.github.sixtysecond.cicdash.jenkins.JenkinsJobQuery;
import com.github.sixtysecond.cicdash.jenkins.JenkinsJobQueryCallable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
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

        try {
            return new JenkinsJobQueryCallable(
                    new JenkinsJobQuery()
                            .setJenkinsServerUrl(jenkinsServerUrl.get())
                            .setJobNamePattern(jenkinsJobPattern.get())
            ).call();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("stackTrace", e.getStackTrace());
            return response;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Timed

    public Response queryJenkins(List<JenkinsJobQuery> jenkinsJobQueryList) {

        JSONArray jsonArray = new JSONArray();
        ExecutorService executorService = Executors.newCachedThreadPool();

        List<Future<JSONObject>> futureResponses = new ArrayList<Future<JSONObject>>();
        for (int i = 0; i < jenkinsJobQueryList.size(); i++) {
            JenkinsJobQuery jenkinsJobQuery = jenkinsJobQueryList.get(i);
            JenkinsJobQueryCallable callable = new JenkinsJobQueryCallable(jenkinsJobQuery);
            Future<JSONObject> futureJsonObject = executorService.submit(callable);
            futureResponses.add(futureJsonObject);
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
            for (Future<JSONObject> futureJsonObject : futureResponses) {
                jsonArray.put(futureJsonObject.get());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getStackTrace())
                    .build();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Response.status(Response.Status.GATEWAY_TIMEOUT)
                    .entity(e.getStackTrace())
                    .build();
        }

        return Response.ok()
                .entity(jsonArray.toString())
                .build();
    }


}