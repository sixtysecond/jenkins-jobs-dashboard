package org.sixtysecond.dashboard;

import com.example.helloworld.resources.JenkinsJobQueryResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQuery;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQueryCallable;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

//import org.testng.annotations.Test;


/**
 * Created by edriggs on 11/2/15.
 */
public class JenkinsJobQueryTest {

    @Test
    public void jenkinsJobQueryGetTest() throws ExecutionException, InterruptedException {
        //        String jenkinsServerUrl = "https://builds.apache.org";
        //        String jobNamePattern = "Ambari.*?";

        String jenkinsServerUrl = "https://builds.apache.org";
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
        String jobNamePattern =  "ActiveMQ.*?";
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
