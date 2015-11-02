package org.sixtysecond.dashboard.service;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.sixtysecond.dashboard.config.HelloWorldConfiguration;
import org.sixtysecond.dashboard.service.health.TemplateHealthCheck;
import org.sixtysecond.dashboard.service.resources.HelloWorldResource;
import org.sixtysecond.dashboard.service.resources.JenkinsQueryResource;

public class JenkinsQueryService extends Service<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new JenkinsQueryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
                    Environment environment) {
        final String template = configuration.getTemplate();
        final String defaultName = configuration.getDefaultName();
        environment.addResource(new HelloWorldResource(template, defaultName));
        environment.addResource(new JenkinsQueryResource());
        environment.addHealthCheck(new TemplateHealthCheck(template));
    }

}