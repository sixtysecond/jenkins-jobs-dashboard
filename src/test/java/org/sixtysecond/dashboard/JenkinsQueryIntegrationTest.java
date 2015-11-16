package org.sixtysecond.dashboard;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sixtysecond.cicdash.CICDashApplication;
import com.github.sixtysecond.cicdash.CICDashConfiguration;
import com.github.sixtysecond.cicdash.jenkins.JenkinsJobQuery;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JenkinsQueryIntegrationTest {
    @Rule
    public final DropwizardAppRule<CICDashConfiguration> RULE =
            new DropwizardAppRule<CICDashConfiguration>(CICDashApplication.class,
                    ResourceHelpers.resourceFilePath("cicdash.yml"));

    @Test
    public void runServerTest() throws JsonProcessingException {
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

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(jenkinsJobQueryList);
        System.out.println("payload=" + payload);
        Response response = client
                .target(String.format("http://localhost:%d/jenkins-job", RULE.getLocalPort()))
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON));
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


}