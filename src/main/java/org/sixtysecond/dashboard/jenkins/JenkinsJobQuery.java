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

public class JenkinsJobQuery implements Callable<JSONObject> {
    private final long id;
    private final String jenkinsServerUrl;
    private final String jobNamePattern;

    public JenkinsJobQuery(String jenkinsServerUrl, String jobNamePattern, long id) {
        this.jenkinsServerUrl = jenkinsServerUrl;
        this.jobNamePattern = jobNamePattern;
        this.id = id;
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

    public JSONObject call() {

        Map<String, JSONObject> jobMap = getJobsForUrl(jenkinsServerUrl);
        Map<String, JSONObject> matchingJobs = filterJsonObjectMap(jobMap, jobNamePattern);
        JenkinsQueryResults jenkinsQueryResults = getLastBuilds(matchingJobs, jenkinsServerUrl);

        JSONObject response = new JSONObject();
        response.put("lastBuilds", jenkinsQueryResults.getLastBuilds());
        response.put("errors", jenkinsQueryResults.getErrors());
        response.put("jobs", matchingJobs);
        response.put("jenkinsServerUrl", jenkinsServerUrl);
        response.put("jobNamePattern", jobNamePattern);
        System.out.println("response=" + response.toString());
        return response;
    }

    public JenkinsQueryResults getLastBuilds(Map<String, JSONObject> matchingJobs, String jenkinsServer) {


        Map<String, JSONObject> lastBuilds = new HashMap<String, JSONObject>();
        Map<String, HttpResponseException> errors = new HashMap<String, HttpResponseException>();

        for (Map.Entry<String, JSONObject> entry : matchingJobs.entrySet()) {
            try {
                lastBuilds.put(entry.getKey(), getLastBuild(jenkinsServer, entry.getKey()));
            } catch (HttpResponseException ex) {
                errors.put(entry.getKey(), ex);
            }
        }

        return new JenkinsQueryResults().setErrors(errors)
                .setLastBuilds(lastBuilds);
    }

    public JSONObject getLastBuild(String jenkinsServer, String jobName) throws HttpResponseException {
        //        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=true".replace("<jenkinsServer>", jenkinsServer)
        //                .replace("<jobName>", jobName);

        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=true".replace("<jenkinsServer>", jenkinsServer)
                .replace("<jobName>", jobName);

        //        System.out.println("lastBuildUrl="+lastBuildUrl);
        Response response = given().when()
                .get(lastBuildUrl)
                .andReturn();

        if (response.statusCode() != 200) {
            throw new HttpResponseException().setStatusCode(response.getStatusCode())
                    .setBody(response.getBody()
                            .asString());
        }
        //        System.out.println("lastBuildResonse="+response.getBody().asString());
        return new JSONObject(response.getBody()
                .asString());


    }

    public Map<String, JSONObject> getJobsForUrl(String jenknisServerUrl) {
        String jobsUrl = jenknisServerUrl + "/api/json?pretty=true";
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
