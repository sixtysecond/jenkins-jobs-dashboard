function pipelineToTable(pipeline) {
    var pipelineHtml = '<div class="pipeline"><tr><td>';

    pipelineHtml += '<div class="pipeline_property">pattern: ' + pipeline.jobNamePattern.toString() + '</div>';
    pipelineHtml += '<div class="pipeline_property">server: ' + pipeline.jenkinsServerUrl.toString() + '</div>';


    if (pipeline.jobs) {

        pipelineHtml += '<fieldset class="jobs"><legend>jobs:</legend>';

        for (var idxJob = 0; idxJob < pipeline.jobs.length; idxJob++) {
            pipelineHtml += '<div class="job">';
            var job = pipeline.jobs[idxJob];

            pipelineHtml += '<div class="job_property">';
            pipelineHtml += '<a href="' + job.lastBuildUrl + '" target="_blank">' + job.name.toString() + '</a>';

            pipelineHtml += '<span class="job_property">';
            pipelineHtml += ' <a href="' + job.lastBuildConsoleUrl + '" target="_blank">console</a>';
            pipelineHtml += '</span>';
            pipelineHtml += '</div>';
            pipelineHtml += '<div>';
            if (job.parameters) {
                pipelineHtml += '<span class="tiny job_property">' + (JSON.stringify(job.parameters)).replace(/\"/g, "");
                pipelineHtml += '</span>';
            }

            pipelineHtml += '</div>';


            var statusClass = getStatusClass(job.status);
            pipelineHtml += '<div class="status_section job_section ">';//75px
            pipelineHtml += '<fieldset class="job_section ' + statusClass + '">';
            pipelineHtml += job.status.toString();
            pipelineHtml += '</fieldset>';
            pipelineHtml += '</div>';

            pipelineHtml += '<div class="age_section job_section" >';
            pipelineHtml += '<fieldset>';
            pipelineHtml += 'age:&nbsp;' + job.age_days;
            pipelineHtml += '&nbsp;days</fieldset>';
            pipelineHtml += '</div>';

            pipelineHtml += '<div class="duration_section job_section">'; //80px
            pipelineHtml += '<fieldset class="job_section">';
            pipelineHtml += 'run:&nbsp;' + job.duration_minutes + ' min';
            pipelineHtml += '</fieldset>';
            pipelineHtml += '</div>';


            if (job.successCount) {
                pipelineHtml += '<fieldset class="job_section">';

                //success
                if (!job.successCount) {
                    job.successCount = '-';
                }
                var successClass = ''
                if (job.successCount > 0) {
                    successClass = "status_success";
                }
                pipelineHtml += ' pass:<fieldset class="' + successClass + '">' + job.successCount + '</fieldset>';

                //failure
                if (!job.failCount) {
                    job.failCount = '-';
                }
                var failureClass = ''
                if (job.failCount > 0) {
                    failureClass = "status_failure";
                }
                pipelineHtml += ' fail:<fieldset class="' + failureClass + '">' + job.failCount + '</fieldset>';

                //skip
                if (!job.skipCount) {
                    job.skipCount = '-';
                }
                var skipClass = ''
                if (job.skipCount > 0) {
                    skipCount = "status_skip";
                }
                pipelineHtml += ' skip:<fieldset class="' + skipClass + '">' + job.skipCount + '</fieldset>';

//                        //total
//                        if (!job.totalCount) {
//                            job.totalCount = '-';
//                        }
//                        var totalClass = ''
//                        if (job.totalCount > 0) {
//                            totalCount = "status_total";
//                        }
//                        pipelineHtml += ' tot:<fieldset class="' + totalClass + '">' + job.totalCount + '</fieldset>';

                pipelineHtml += '</fieldset> <!-- end job_section -->';

            }


            pipelineHtml += '</div><!--end job div-->';
        }
        pipelineHtml += '</fieldset>';
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
    document.getElementById('dashboard').innerHTML = pipelinesToHtml(pipelines);
}