package com.pingidentity.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This will configure the Jackson object mapper to work with DynamoDB and lower the chances of it breaking
 * if an upstream provider changes their request. Also simplifies the need to muddy our Request/Response objects
 * with overrides and nullable notations.
 */
@Configuration
@Slf4j
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper getObjectMapper() {
        log.info("objectMapper - enter");
        ObjectMapper objectMapper = new ObjectMapper();
        //Lets a new property that has been added upstream not blow up this app
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        //Will not serialize null or empty json elements -- useful for DynamoDB since that gets picky with those.
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        log.info("objectMapper - exit");
        return objectMapper;
    }
}
