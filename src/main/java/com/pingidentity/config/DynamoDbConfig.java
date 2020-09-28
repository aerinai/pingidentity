package com.pingidentity.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This assumes that the application will be running in AWS and it has proper permissions to DynamoDB
 *
 * In the future, should the application need to be ran in a non-AWS cloud, or on a local dev box, we may
 * need to expand this.
 */
@Configuration
@Slf4j
public class DynamoDbConfig {
    @Value("${dynamoDb.url}")
    String dynamoDbUrl;
    @Value("${dynamoDb.region}")
    String dynamoDbRegion;

    @Bean
    public DynamoDB getDynamoDbConfig() {
        log.trace("getDynamoDbConfig - enter");
        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(dynamoDbUrl, dynamoDbRegion)).build();
        DynamoDB result = new DynamoDB(client);
        log.trace("getDynamoDbConfig - exit");
        return result;
    }
}
