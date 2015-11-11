package com.example.helloworld;

import com.example.helloworld.health.TemplateHealthCheck;
import com.example.helloworld.resources.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import io.dropwizard.Application;
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

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
                    Environment environment) {

        environment.jersey().register(new IndexResource());
        environment.jersey().register(new JenkinsJobQueryResource());
        environment.getObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

//        environment.getObjectMapper().setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
//                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
//                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
//                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
//                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);



    }

}