package org.sixtysecond.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.json.JSONObject;
import org.junit.Test;
import com.github.sixtysecond.cicdash.jenkins.JenkinsJobQuery;
import com.github.sixtysecond.cicdash.jenkins.JenkinsJobQueryCallable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by edriggs on 11/9/15.
 */
public class SerializeTest {


    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void doDeserializeTest() throws IOException {

        List<JenkinsJobQuery> jenkinsJobQueryList =
                MAPPER.readValue(fixture("fixtures/jenkinsJobQuery2.json"), new TypeReference<List<JenkinsJobQuery>>() {
        });
        System.out.println("jenkinsJobQueryList=" + new ReflectionToStringBuilder(jenkinsJobQueryList).toString());
        assertThat(jenkinsJobQueryList.size(), is(2));
        System.out.println(jenkinsJobQueryList.get(0));
    }

    @Test
    public void serializeTwoItemsTest() throws JsonProcessingException {
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

        assertThat(MAPPER.writeValueAsString(jenkinsJobQueryList)
                , is(fixture("fixtures/jenkinsJobQuery2.json")));
    }


    @Test
    public void serializeOneItemTest() throws Exception {
        final List<JenkinsJobQuery> jenkinsJobQueryList = new ArrayList<JenkinsJobQuery>();
        jenkinsJobQueryList.add(new JenkinsJobQuery().setJenkinsServerUrl("https://builds.apache.org")
                .setJobNamePattern("Accumulo-1.7"));
        assertThat(MAPPER.writeValueAsString(jenkinsJobQueryList)
                , is(fixture("fixtures/jenkinsJobQuery1.json")));
    }


    @Test
    public void serializeJsonObjectTest() throws JsonProcessingException, ExecutionException, InterruptedException {

        JenkinsJobQuery jenkinsJobQuery = new JenkinsJobQuery().setJenkinsServerUrl("https://builds.apache.org")
                .setJobNamePattern("Accumulo-1.7");
        JSONObject jsonObject = new JenkinsJobQueryCallable(jenkinsJobQuery).call();
        System.out.println(jsonObject.toString());

    }

}
