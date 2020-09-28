package com.pingidentity;
import lombok.extern.slf4j.Slf4j;
import nonapi.io.github.classgraph.utils.Join;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Your assignment is to write a method which returns a list of JIRA
 * tickets (as String). This will be used as a library in a bigger flow:
 * one program will provide the list of GitCommits, and another
 * will consume the list of Strings provided to do stuff on JIRA.
 */
@SpringBootApplication
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
@Slf4j
public class Application
{
    public static void main(String[] args) {
        log.warn("MAIN ENTRY: " + args.length);
        StringBuilder sb = new StringBuilder();
        for(String arg : args) {
            log.warn("LOOP: " + arg);
            sb.append(arg);
            sb.append("-");
        }
        log.warn("STARTING APP: " + sb.toString());
        SpringApplication.run(Application.class, args);
    }

}
