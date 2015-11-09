package org.sixtysecond.dashboard.jenkins;

import com.jayway.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by edriggs on 11/9/15.
 */
public class JenkinsJobQueryCallable implements Callable<JSONObject> {

    private final JenkinsJobQuery jenkinsJobQuery;

    public JenkinsJobQueryCallable(JenkinsJobQuery jenkinsJobQuery) {
        this.jenkinsJobQuery = jenkinsJobQuery;
    }

    public JSONObject call() {

        Map<String, JSONObject> jobMap = getJobsForUrl(jenkinsJobQuery.getJenkinsServerUrl());
        Map<String, JSONObject> matchingJobs = filterJsonObjectMap(jobMap, jenkinsJobQuery.getJobNamePattern());
        Map<String, LastBuildResponse> lastBuilds = getLastBuilds(matchingJobs, jenkinsJobQuery.getJenkinsServerUrl());

        JSONObject response = new JSONObject();
        response.put("lastBuilds", lastBuilds);
        response.put("jobs", matchingJobs);
        response.put("jenkinsServerUrl", jenkinsJobQuery.getJenkinsServerUrl());
        response.put("jobNamePattern", jenkinsJobQuery.getJobNamePattern());
        return response;
    }

    public Map<String, LastBuildResponse> getLastBuilds(Map<String, JSONObject> matchingJobs, String jenkinsServer) {


        Map<String, LastBuildResponse> jsonResponseMap = new HashMap<String, LastBuildResponse>();

        for (Map.Entry<String, JSONObject> entry : matchingJobs.entrySet()) {
            LastBuildResponse lastBuildResponse = getLastBuild(jenkinsServer, entry.getKey());
            jsonResponseMap.put(entry.getKey(), lastBuildResponse);
        }
        return jsonResponseMap;
    }

    public LastBuildResponse getLastBuild(String jenkinsServer, String jobName) {
        //        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=true".replace("<jenkinsServer>", jenkinsServer)
        //                .replace("<jobName>", jobName);

        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=false".replace("<jenkinsServer>", jenkinsServer)
                .replace("<jobName>", jobName);

        //        System.out.println("lastBuildUrl="+lastBuildUrl);
        Response response = given().when()
                .get(lastBuildUrl)
                .andReturn();

        String message = "";
        JSONObject jsonObject = null;
        if (response.statusCode() == 200) {
            try {
                jsonObject = new JSONObject(response.asString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        String body = "";
        if (response.getBody() != null) {
            body = response.getBody()
                    .asString();
        }

        return new LastBuildResponse()
                //.setMessage(body)
                .setLastBuild(jsonObject)
                .setStatusCode(response.getStatusCode());


    }

    public Map<String, JSONObject> getJobsForUrl(String jenknisServerUrl) {
        String jobsUrl = jenknisServerUrl + "/api/json?pretty=false";
        //        System.out.println("jobsUrl="+jobsUrl);
        Response response = given().when()
                .get(jobsUrl)
                .andReturn();

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
