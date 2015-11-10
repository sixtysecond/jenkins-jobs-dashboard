package org.sixtysecond.dashboard;

import com.example.helloworld.resources.JenkinsJobQueryResource;
import com.example.helloworld.resources.JenkinsJobQuerySingleResource;
import org.json.JSONObject;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQuery;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQueryCallable;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by edriggs on 11/2/15.
 */
public class JenkinsJobQueryTest {

    @Test

    public void jenkinsJobQueryGetTest() {
        //        String jenkinsServerUrl = "https://builds.apache.org";
        //        String jobNamePattern = "Ambari.*?";

        String jenkinsServerUrl = "https://builds.apache.org";
        String jobNamePattern = "Accumulo-1.7";
        JenkinsJobQuery jenkinsJobQuery = new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern);
        JSONObject response = new JenkinsJobQueryCallable(jenkinsJobQuery).call();
        System.out.println("response=" + response);
    }


    @Test
    public void jenkinsJobQueryPostTest() {
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


        System.out.println("jsonArray=" + response.getEntity()
                .toString());

    }


    @Test
    public void jenkinsJobQuerySinglePostTest() {

        String jenkinsServerUrl = "https://builds.apache.org";
        String jobNamePattern = "Accumulo-1.6";
        JenkinsJobQuery jenkinsJobQuery = new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern);

        Response response = new JenkinsJobQuerySingleResource().queryJenkins(jenkinsJobQuery);
        assertThat(response.getStatus(), is(200));


        System.out.println("jsonArray=" + response.getEntity()
                .toString());

    }
}
