function pipelineToTable(pipeline) {
    var pipelineHtml = '<div class="pipeline"><tr><td>';

    pipelineHtml += '<div class="query_wrapper">';
    pipelineHtml += '<fieldset  class="query"><legend>query</legend>';
    pipelineHtml += '<div class="pipeline_property">pattern: ' + pipeline.jobNamePattern.toString() + '</div>';
    var serverUrl = pipeline.jenkinsServerUrl.toString();
    pipelineHtml += '<div class="pipeline_property">server: <a href="' + serverUrl + '">' + serverUrl + '</a></div>';
    pipelineHtml += '</fieldset>';
    pipelineHtml += '</div><!--end query_wrapper-->';

    if (pipeline.jobs) {
        pipelineHtml += '<div class="jobs_wrapper">';
        pipelineHtml += '<fieldset class="jobs"><legend>jobs:</legend>';

        for (var idxJob = 0; idxJob < pipeline.jobs.length; idxJob++) {
            pipelineHtml += '<div class="job">';
            var job = pipeline.jobs[idxJob];


            pipelineHtml += '<div class="job_links">';
            //pipelineHtml += '<fieldset>';
            pipelineHtml += '<span class="job_last_build">';
            pipelineHtml += '<a href="' + job.lastBuildUrl + '" target="_blank">' + job.name.toString() + '</a>';
            pipelineHtml += '</span>';
            //pipelineHtml += '</fieldset>';

            //pipelineHtml += '&nbsp;<fieldset>';
            pipelineHtml += '&nbsp;&nbsp;<span class="job_last_build_console">';
            pipelineHtml += '<a href="' + job.lastBuildConsoleUrl + '" target="_blank">console</a>';
            pipelineHtml += '</span>';
            //pipelineHtml += '</fieldset>';
            pipelineHtml += '</div><!--end job links-->';


            if (job.parameters) {
                pipelineHtml += '<div class="parameters">';
                pipelineHtml += (JSON.stringify(job.parameters)).replace(/\"/g, "");
                pipelineHtml += '</div>';
            }


            var statusClass = getStatusClass(job.status);
            pipelineHtml += '<div class="last_build_properties">';
            pipelineHtml += '<fieldset class="property_bubble ' + statusClass + '">';
            pipelineHtml += job.status.toString();
            pipelineHtml += '</fieldset>';
            //pipelineHtml += '</div>';

            pipelineHtml += '<span class="property_label">&nbsp;age:</span>';
            pipelineHtml += '<fieldset class="property_bubble">';
            pipelineHtml += job.age_days;
            pipelineHtml += '<span class="time_unit">&nbsp;days</span>';
            pipelineHtml += '</fieldset>';

            pipelineHtml += '<span class="property_label">&nbsp;run:</span>';
            pipelineHtml += '<fieldset class="property_bubble">';
            pipelineHtml += job.duration_minutes;
            pipelineHtml += '<span class="time_unit">&nbsp;min</span>';
            pipelineHtml += '</fieldset>';

            if (job.successCount) {
                //pipelineHtml += '<fieldset class="job_section">';

                //success
                if (!job.successCount) {
                    job.successCount = '-';
                }
                var successClass = ''
                if (job.successCount > 0) {
                    successClass = "status_success";
                }
                pipelineHtml += ' pass:<fieldset class="property_bubble ' + successClass + '">' + job.successCount + '</fieldset>';

                //failure
                if (!job.failCount) {
                    job.failCount = '-';
                }
                var failureClass = ''
                if (job.failCount > 0) {
                    failureClass = "status_failure";
                }
                pipelineHtml += ' fail:<fieldset class="property_bubble ' + failureClass + '">' + job.failCount + '</fieldset>';

                //skip
                if (!job.skipCount) {
                    job.skipCount = '-';
                }
                var skipClass = '';
                if (job.skipCount > 0) {
                    skipClass = "status_skip";
                }
                pipelineHtml += ' skip:<fieldset class="property_bubble ' + skipClass + '">' + job.skipCount + '</fieldset>';

//                        //total
//                        if (!job.totalCount) {
//                            job.totalCount = '-';
//                        }
//                        var totalClass = ''
//                        if (job.totalCount > 0) {
//                            totalCount = "status_total";
//                        }
//                        pipelineHtml += ' tot:<fieldset class="' + totalClass + '">' + job.totalCount + '</fieldset>';

                //pipelineHtml += '</fieldset> <!-- end job_section -->';

            }
            pipelineHtml += '</div><!--end last_build_properties-->';

            pipelineHtml += '</div><!--end job div-->';
        }
        pipelineHtml += '</fieldset><!--end jobs fieldset-->';
        pipelineHtml += '</div><!--end jobs wrapper-->';
    }
    pipelineHtml += '</div><!--end pipeline div-->';
    return pipelineHtml;
}

function getStatusClass(status) {
    if (!status) {
        return '';
    }

    status = status.toString().toLowerCase();
    if (status == 'success') {
        return 'status_success';
    }
    else if (status == 'failure') {
        return 'status_failure';
    }
    return 'status_unknown';
}
function pipelinesToHtml(pipelines) {


    var dashboard = '';
    dashboard += '<div style="display:inline-block"><button onclick="postJobQuery()" style="display:inline-block">refresh</button></div>';
    dashboard += '<div class="dashboard" id="dashboard" name="dashboard">';


    for (var idxPipeline = 0; idxPipeline < pipelines.length; idxPipeline++) {
        var pipeline = pipelines[idxPipeline];
        var pipelineHtml = pipelineToTable(pipeline);
        dashboard += pipelineHtml;
    }

    dashboard += '</div><!--end dashboard div -->';

    return dashboard;

}

function renderDashboard(pipelines) {
    document.getElementById('dashboard_wrapper').innerHTML = pipelinesToHtml(pipelines);
}