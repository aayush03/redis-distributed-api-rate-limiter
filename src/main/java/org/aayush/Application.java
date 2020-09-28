package org.aayush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.WebApplicationInitializer;

/**
 * @author Aayush Srivastava
 */
@SpringBootApplication(scanBasePackages = "org.aayush.*")
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "org.aayush")
@PropertySource("application.properties")
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
