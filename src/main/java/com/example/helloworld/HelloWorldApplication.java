package com.example.helloworld;

import com.example.helloworld.resources.IndexResource;
import com.example.helloworld.resources.JenkinsJobQueryResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    //    @Override
    //    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    //        // nothing to do yet
    //    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {

        bootstrap.addBundle(new AssetsBundle());
        //        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
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