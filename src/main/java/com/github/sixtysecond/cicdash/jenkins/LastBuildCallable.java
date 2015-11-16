package com.github.sixtysecond.cicdash.jenkins;

import com.jayway.restassured.response.Response;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by edriggs on 11/11/15.
 */
public class LastBuildCallable implements Callable {

    private final String jenkinsServer;
    private final String jobName;

    public LastBuildCallable(

            String jenkinsServer, String jobName
    ) {
        this.jenkinsServer = jenkinsServer;
        this.jobName = jobName;
    }

    public LastBuildResponse call() {
        //        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=true".replace("<jenkinsServer>", jenkinsServer)
        //                .replace("<jobName>", jobName);

        String lastBuildUrl = "<jenkinsServer>/job/<jobName>/lastBuild/api/json?pretty=false".replace("<jenkinsServer>", jenkinsServer)
                .replace("<jobName>", jobName);

        //        System.out.println("lastBuildUrl="+lastBuildUrl);
        Response response = given().when()
                .relaxedHTTPSValidation()
                .redirects().follow(true)
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
}
