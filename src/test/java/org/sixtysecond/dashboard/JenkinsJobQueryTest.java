package org.sixtysecond.dashboard;

import com.jayway.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.*;
import java.util.regex.Pattern;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by edriggs on 11/2/15.
 */
public class JenkinsJobQueryTest {

    @Test

    public void jenkinsJobQueryTest() {

        String jenknisServerUrl = "https://builds.apache.org";
        String jobNamePattern = "Ambari.*?";

        Map<String, JSONObject> jobMap = getJobsForUrl(jenknisServerUrl);
        Map<String, JSONObject> matchingJobs = filterJsonObjectMap(jobMap, jobNamePattern);
        Map<String, JSONObject> lastBuilds = getLastBuilds(matchingJobs, jenknisServerUrl);

        JSONObject response = new JSONObject();
        response.put("lastBuild", lastBuilds);
        response.put("job", matchingJobs);
        System.out.println("response=" + response.toString());
    }

    public Map<String, JSONObject> getLastBuilds(Map<String, JSONObject> matchingJobs, String jenkinsServer) {
        Map<String, JSONObject> lastBuilds = new HashMap<String, JSONObject>();

        for (Map.Entry<String, JSONObject> entry : matchingJobs.entrySet()) {
            try {
                lastBuilds.put(entry.getKey(), getLastBuild(jenkinsServer, entry.getKey()));
            } catch (AssertionError err) {
                err.printStackTrace();
            }
        }
        return lastBuilds;
    }

    public JSONObject getLastBuild(String jenkinsServer, String jobName) {
        //        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=true".replace("<jenkinsServer>", jenkinsServer)
        //                .replace("<jobName>", jobName);

        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=true".replace("<jenkinsServer>", jenkinsServer)
                .replace("<jobName>", jobName);


        //        System.out.println("lastBuildUrl="+lastBuildUrl);
        Response response = given().when()
                .get(lastBuildUrl)
                .andReturn();

        assertThat(lastBuildUrl, response.getStatusCode(), is(200));
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
