package org.sixtysecond.dashboard;

import com.github.sixtysecond.cicdash.endpoint.JenkinsJobQueryResource;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import com.github.sixtysecond.cicdash.jenkins.JenkinsJobQuery;
import com.github.sixtysecond.cicdash.jenkins.JenkinsJobQueryCallable;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

//import org.testng.annotations.Test;


/**
 * Created by edriggs on 11/2/15.
 */
public class JenkinsJobQueryTest {

    protected static WireMockServer wireMockServer = new WireMockServer();

    public static void stubJobFixtureGet(String fixtureName) {
        stubFor(WireMock.get(urlEqualTo("job/" + fixtureName + "/api/json.*?"))
                .willReturn(aResponse().withBody(fixture("fixtures/" + fixtureName + ".json"))));
    }

    @BeforeClass
    public static void beforeClass() {
        wireMockServer.start();


        stubFor(WireMock.get(urlEqualTo("/api/json.*?"))
                .willReturn(aResponse().withBody(fixture("fixtures/jobs.json"))));

        stubJobFixtureGet(FixtureJson.ACCUMULO_1_6);
        stubJobFixtureGet(FixtureJson.ACCUMULO_1_7);
        stubJobFixtureGet(FixtureJson.ACCUMULO_MASTER);
        stubJobFixtureGet(FixtureJson.ACCUMULO_PULL_REQUESTS);



    }


    @AfterClass
    public static void stopServer() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wireMockServer.stop();
    }

    @After
    public void after() {
        WireMock.shutdownServer();
    }

    @Test
    public void jenkinsJobQueryGetTest() throws ExecutionException, InterruptedException {
        //        String jenkinsServerUrl = "https://builds.apache.org";
        //        String jobNamePattern = "Ambari.*?";
        System.out.println(wireMockServer.listAllStubMappings());

        String jenkinsServerUrl = "http://localhost:8080";
        String jobNamePattern = "Accumulo-1.7";
        JenkinsJobQuery jenkinsJobQuery = new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern);
        JSONObject response = new JenkinsJobQueryCallable(jenkinsJobQuery).call();
        System.out.println("response=" + response);
    }

    @Test
    public void jenkinsJobQueryGetMultipleJobsTest() throws ExecutionException, InterruptedException {
        //        String jenkinsServerUrl = "https://builds.apache.org";
        //        String jobNamePattern = "Ambari.*?";

        String jenkinsServerUrl = "https://builds.apache.org";
        String jobNamePattern = "ActiveMQ.*?";
        JenkinsJobQuery jenkinsJobQuery = new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern);
        JSONObject response = new JenkinsJobQueryCallable(jenkinsJobQuery).call();
        System.out.println("response=" + response);
    }


    //@Test //disabled for faster debug build
    public void jenkinsJobQueryPostMultiplePatternsTest() {
        List<JenkinsJobQuery> jenkinsJobQueryList = new ArrayList<JenkinsJobQuery>();
        {
            String jenkinsServerUrl = "https://builds.apache.org";
            String jobNamePattern = "Accumulo-1.6";
            jenkinsJobQueryList.add(new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern));
        }
        {
            String jenkinsServerUrl = "https://builds.apache.org";
            String jobNamePattern = "Accumulo-1.7";
            jenkinsJobQueryList.add(new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern));
        }

        Response response =
                new JenkinsJobQueryResource()
                        .queryJenkins(jenkinsJobQueryList);
        assertThat(response.getStatus(), is(200));
        String jsonString = (String) response.getEntity();
        JSONArray jsonArray = new JSONArray(jsonString);
        assertThat(jsonArray.length(), is(greaterThan(0)));
        System.out.println("jsonArray=" + response.getEntity()
                .toString());

    }

    // @Test //disabled for faster debug build
    public void jenkinsJobQueryPostMultipleJobMatchedTest() {
        List<JenkinsJobQuery> jenkinsJobQueryList = new ArrayList<JenkinsJobQuery>();
        {
            String jenkinsServerUrl = "https://builds.apache.org";
            String jobNamePattern = "ActiveMQ.*?";
            jenkinsJobQueryList.add(new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern));
        }

        Response response =
                new JenkinsJobQueryResource()
                        .queryJenkins(jenkinsJobQueryList);
        assertThat(response.getStatus(), is(200));
        String jsonString = (String) response.getEntity();
        JSONArray jsonArray = new JSONArray(jsonString);
        assertThat(jsonArray.length(), is(1));


        System.out.println("jsonArray=" + response.getEntity()
                .toString());

    }

}
