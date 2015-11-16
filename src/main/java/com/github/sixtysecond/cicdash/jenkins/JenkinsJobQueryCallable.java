package com.github.sixtysecond.cicdash.jenkins;

import com.jayway.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by edriggs on 11/9/15.
 */
public class JenkinsJobQueryCallable implements Callable<JSONObject> {

    private final JenkinsJobQuery jenkinsJobQuery;


    public JenkinsJobQueryCallable( JenkinsJobQuery jenkinsJobQuery) {
        this.jenkinsJobQuery = jenkinsJobQuery;
    }

    public JSONObject call() throws ExecutionException, InterruptedException {

        Map<String, JSONObject> jobMap = getJobsForUrl(jenkinsJobQuery.getJenkinsServerUrl());
        Map<String, JSONObject> matchingJobs = filterJsonObjectMap(jobMap, jenkinsJobQuery.getJobNamePattern());
        Map<String, LastBuildResponse> lastBuilds = getLastBuilds(matchingJobs, jenkinsJobQuery.getJenkinsServerUrl());

        JSONObject response = new JSONObject();
        response.put("lastBuilds", lastBuilds);
        response.put("jobs", matchingJobs);
        response.put("jenkinsServerUrl", jenkinsJobQuery.getJenkinsServerUrl());
        response.put("jobNamePattern",  jenkinsJobQuery.getJobNamePattern());
        return response;
    }

    public Map<String, LastBuildResponse> getLastBuilds(Map<String, JSONObject> matchingJobs, String jenkinsServer)
            throws InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        Map<String, Future<LastBuildResponse>> futureJsonResponseMap = new HashMap<String, Future<LastBuildResponse>>();

        for (Map.Entry<String, JSONObject> entry : matchingJobs.entrySet()) {
            LastBuildCallable lastBuildCallable = new LastBuildCallable(jenkinsServer, entry.getKey());
            Future<LastBuildResponse> futureLastBuildResponse = executorService.submit(lastBuildCallable);
            futureJsonResponseMap.put(entry.getKey(), futureLastBuildResponse);
        }
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        Map<String, LastBuildResponse> jsonResponseMap = new HashMap<String, LastBuildResponse>();
        for ( Map.Entry<String, Future<LastBuildResponse>> entry : futureJsonResponseMap.entrySet()) {
            jsonResponseMap.put(entry.getKey(), entry.getValue().get());
        }
        return jsonResponseMap;
    }



    public Map<String, JSONObject> getJobsForUrl(String jenknisServerUrl) {
        String jobsUrl = jenknisServerUrl + "/api/json?pretty=false";
                System.out.println("jobsUrl="+jobsUrl);
        Response response = given().when()
                .relaxedHTTPSValidation()
                .redirects().follow(true)
                .get(jobsUrl)

                .andReturn();

        if (response.getStatusCode() != 200) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("statusCode", response.getStatusCode());
            jsonObject.put("body", response.getBody());
            Map<String,JSONObject> ret = new HashMap<String,JSONObject>();
            ret.put(jobsUrl, jsonObject);
            return ret;
        }
        assertThat(response.getStatusCode(), is(200));
        //        System.out.println("jobsResponse="+response.asString());

        JSONObject jsonObject = new JSONObject(response.getBody()
                .asString());
        JSONArray jsonArray = jsonObject.getJSONArray("jobs");

        Map<String, JSONObject> jsonObjectMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject job = jsonArray.getJSONObject(i);
            String jobName = job.getString("name");
            jsonObjectMap.put(jobName, job);
        }
        return jsonObjectMap;
    }

    public Map<String, JSONObject> filterJsonObjectMap(Map<String, JSONObject> jsonObjectMap, String regex) {
        Map<String, JSONObject> matchingMap = new HashMap<String, JSONObject>();
        Pattern pattern = Pattern.compile(regex);
        for (Map.Entry<String, JSONObject> entry : jsonObjectMap.entrySet()) {

            if (pattern.matcher(entry.getKey())
                    .matches()) {
                matchingMap.put(entry.getKey(), entry.getValue());
            }
        }
        return matchingMap;
    }
}
