package org.sixtysecond.dashboard;


import com.example.helloworld.HelloWorldApplication;
import com.example.helloworld.HelloWorldConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQuery;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JenkinsQueryIntegrationTest {
    @Rule
    public final DropwizardAppRule<HelloWorldConfiguration> RULE =
            new DropwizardAppRule<HelloWorldConfiguration>(HelloWorldApplication.class,
                    ResourceHelpers.resourceFilePath("hello-world.yml"));

    @Test
    public void runServerTest() {
        Client client = new JerseyClientBuilder().build();

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

        Response response = client.target(
                String.format("http://localhost:%d/jenkins-job-single", RULE.getLocalPort())
        )
                .request()
                .post(Entity.entity(jenkinsJobQueryList, MediaType.APPLICATION_JSON));
        System.out.println("headers=" + response.getStringHeaders());
        System.out.println("statusInfo=" + response.getStatusInfo()
                .toString());
        ;
        if (response.hasEntity()) {

            System.out.println("entity=" + response.getEntity()
                    .toString());
        } else {
            System.out.println("response=" + response.toString());
        }
        assertThat(response.getStatus()).isEqualTo(200);

    }

    @Test
    public void runSingleServerTest() {
        Client client = new JerseyClientBuilder().build();

        String jenkinsServerUrl = "https://builds.apache.org";
        String jobNamePattern = "Accumulo-1.7";
        JenkinsJobQuery jenkinsJobQuery = new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern);

        Response response = client.target(
                String.format("http://localhost:%d/jenkins-job-single", RULE.getLocalPort())
        )
                .request()
                .post(Entity.entity(jenkinsJobQuery, MediaType.APPLICATION_JSON));
        System.out.println("headers=" + response.getStringHeaders());
        System.out.println("statusInfo=" + response.getStatusInfo()
                .toString());
        System.out.println("status=" + response.getStatus());


        if (response.hasEntity()) {
            String responseString =  response.readEntity(String.class);

            System.out.println("responseString="+responseString);
        } else {
            System.out.println("response=" + response.toString());
        }
        assertThat(response.getStatus()).isEqualTo(200);

    }
}