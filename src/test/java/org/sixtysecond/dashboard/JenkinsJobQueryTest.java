package org.sixtysecond.dashboard;

import org.json.JSONObject;
import org.sixtysecond.dashboard.jenkins.JenkinsJobQuery;
import org.testng.annotations.Test;


/**
 * Created by edriggs on 11/2/15.
 */
public class JenkinsJobQueryTest  {

    @Test

    public void jenkinsJobQueryTest() {
        String jenkinsServerUrl = "https://builds.apache.org";
        String jobNamePattern = "Ambari.*?";

        JenkinsJobQuery jenkinsJobQuery = new JenkinsJobQuery(jenkinsServerUrl, jobNamePattern, System.currentTimeMillis());
        JSONObject response = jenkinsJobQuery.call();
        System.out.println("response="+response);
    }


}
