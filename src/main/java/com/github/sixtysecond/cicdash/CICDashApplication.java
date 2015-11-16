package com.github.sixtysecond.cicdash;

import com.github.sixtysecond.cicdash.endpoint.IndexResource;
import com.github.sixtysecond.cicdash.endpoint.JenkinsJobQueryResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CICDashApplication extends Application<CICDashConfiguration> {
    public static void main(String[] args) throws Exception {
        new CICDashApplication().run(args);
    }

    @Override
    public String getName() {
        return "cicdash";
    }


    @Override
    public void initialize(Bootstrap<CICDashConfiguration> bootstrap) {

        bootstrap.addBundle(new AssetsBundle());
        //        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
    }

    @Override
    public void run(CICDashConfiguration configuration,
                    Environment environment) {

        environment.jersey()
                .register(new IndexResource());
        environment.jersey()
                .register(new JenkinsJobQueryResource());
        //        environment.getObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        environment.jersey()
                .setUrlPattern("/assets/*");

        //        final TemplateHealthCheck healthCheck =
        //                new TemplateHealthCheck(configuration.getTemplate());
        //        environment.healthChecks().register("template", healthCheck);


    }

}