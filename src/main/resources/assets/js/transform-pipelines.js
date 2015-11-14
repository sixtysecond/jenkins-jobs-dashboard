function postJobQuery() {

    var xmlhttp;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        // code for older browsers
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            //TODO: parse data
//                    document.getElementById("result").innerHTML =
//                            xmlhttp.responseText;
            var responses = JSON.parse(xmlhttp.responseText);
            var pipelines = processResponses(responses);
            renderDashboard(pipelines);
        }
    }

    xmlhttp.open("POST", "/jenkins-job", true);

    xmlhttp.setRequestHeader("Content-Type", "application/json");

    xmlhttp.send(jobQuery.value);

}

function processResponses(responses) {
    document.getElementById('rawJson').value = JSON.stringify(responses, null, '\t');
    var pipelines = transformResponsesToPipelines(responses);
    document.getElementById('transformedJson').value = JSON.stringify(pipelines, null, '\t');
    return pipelines;
}

function transformResponsesToPipelines(responses) {
    var pipelines = [];
    for (var idxPipeline = 0; idxPipeline < responses.length; idxPipeline++) {
        var pipeline = {};

        var response = responses[idxPipeline];

        pipeline.jenkinsServerUrl = response['jenkinsServerUrl'];
        pipeline.jobNamePattern = response['jobNamePattern'];

        var responseJobs = response['jobs'];

        var responseLastBuilds = response['lastBuilds'];

        var responseJobKeys = Object.keys(responseJobs);


        var jobs = [];
        for (var idxJob = 0; idxJob < responseJobKeys.length; idxJob++) {
            var job = {};

            job.name = responseJobKeys[idxJob];
            job.url = responseJobs[job.name]['url'];
            job.statusCode = responseLastBuilds[job.name]['statusCode'];
            var lastBuild = responseLastBuilds[job.name]['lastBuild'];

            if (lastBuild) {

                job.lastBuildUrl = lastBuild['url'];

                if (job.lastBuildUrl) {
                    job.lastBuildConsoleUrl = job.lastBuildUrl + "console";
                }


                job.duration = lastBuild['duration'];
                job.duration_minutes = (job.duration / 1000 / 60).toFixed(1);
                job.timestamp = lastBuild['timestamp'];

                job.nowMillis = new Date().getTime();
                job.millisSinceBuild = job.nowMillis - job.timestamp;
                job.age_days = (job.millisSinceBuild / 1000 / 60 / 60 / 24).toFixed(1);
                job.result = lastBuild['result'];
                var lastBuildActions = lastBuild['actions'];


                job.start = new Date(job.timestamp);
                job.end = new Date(job.timestamp + job.duration);
                job.building = lastBuild['building'];


                for (var idxAction = 0; idxAction < lastBuildActions.length; idxAction++) {
                    var lastBuildAction = lastBuildActions[idxAction];
                    var actionKeys = Object.keys(lastBuildAction);

                    if (actionKeys.indexOf('failCount') > -1) {
                        job.failCount = lastBuildAction['failCount'];
                    }

                    if (actionKeys.indexOf('totalCount') > -1) {
                        job.totalCount = lastBuildAction['totalCount'];
                    }

                    if (actionKeys.indexOf('skipCount') > -1) {
                        job.skipCount = lastBuildAction['skipCount'];
                    }

                    if (actionKeys.indexOf('parameters') > -1) {
                        var parametersArray = lastBuildAction['parameters']

                        job.parameters = {};
                        for (var idxParameter = 0; idxParameter < parametersArray.length; idxParameter++) {
                            var parameter = parametersArray[idxParameter];

                            if (parameter['name']) {
                                job.parameters[parameter['name']] = parameter['value'];
                            }
                        }
                    }
                }
                if (job.totalCount) {
                    job.successCount = job.totalCount - job.failCount - job.skipCount;
                }
            }
            if (job.result) {
                job.status = job.result;
            }
            else {
                job.status = job.statusCode;
            }

            jobs.push(job);
        }

        var sortedJobs = sortJobsByTimeStamp(jobs);
        pipeline.jobs = sortedJobs;
        //pipeline.jobs = jobs;
        pipelines.push(pipeline);
    }
    return pipelines;
}

function sortJobsByTimeStamp(unsortedJobs) {

    var jobTimeStampMap = {};
    for (var idxJob = 0; idxJob < unsortedJobs.length; idxJob++) {

        var job = unsortedJobs[idxJob];
        var jobName = job['name'];
        var timestamp = job['timestamp'];
        if (!timestamp) {
            timestamp = '0000';
        }
        var key = timestamp + '-' + jobName;
        jobTimeStampMap[key] = job;
    }
    var sortedJobs = [];
    var keysUnsorted = Object.keys(jobTimeStampMap);
    var keysSorted = keysUnsorted.sort().reverse();

    for (var idxKey = 0; idxKey < keysSorted.length; idxKey++) {
        var key = keysSorted[idxKey];
        sortedJobs.push(jobTimeStampMap[key]);
    }

    return sortedJobs;

}