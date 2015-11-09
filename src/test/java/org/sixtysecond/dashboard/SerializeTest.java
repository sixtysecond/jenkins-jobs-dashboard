package org.sixtysecond.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQuery;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by edriggs on 11/9/15.
 */
public class SerializeTest {


    @Test
    public void doSerializeTest() throws JsonProcessingException {
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

        String expected="[{\"jenkinsServerUrl\":\"https://builds.apache.org\",\"jobNamePattern\":\"Accumulo-1.6\"},{\"jenkinsServerUrl\":\"https://builds.apache.org\",\"jobNamePattern\":\"Accumulo-1.7\"}]";
        ObjectMapper mapper = new ObjectMapper();
        final String actual = mapper.writeValueAsString(jenkinsJobQueryList);
        System.out.println("payload=" + actual);
        assertThat(actual, is(expected));
    }

    @Test
    public void doDeserializeTest() throws IOException {
        String json="[{\"jenkinsServerUrl\":\"https://builds.apache.org\",\"jobNamePattern\":\"Accumulo-1.6\"},{\"jenkinsServerUrl\":\"https://builds.apache.org\",\"jobNamePattern\":\"Accumulo-1.7\"}]";
        System.out.println("json="+json);
        ObjectMapper mapper = new ObjectMapper();

        List<JenkinsJobQuery> jenkinsJobQueryList = mapper.readValue(json, new TypeReference<List<JenkinsJobQuery>>(){});
        System.out.println("jenkinsJobQueryList="+ new ReflectionToStringBuilder(jenkinsJobQueryList).toString());
        assertThat(jenkinsJobQueryList.size(), is(2));

//        List<MyClass> myObjects = mapper.readValue(jsonInput, mapper.getTypeFactory().constructCollectionType(List.class, MyClass.class));
//
//        mapper.readValue(jsonArray, List<JenkinsJobQuery>)
     }

}
