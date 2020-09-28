package com.pingidentity.config;

import com.pingidentity.dao.commitDao.DynamoDbDao;
import com.pingidentity.dao.commitDao.ICommitDao;
import com.pingidentity.dao.commitDao.SimpleBackendDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This will let us determine what storage engine will be used by the application; a development backend, or a 'real'
 * DynamoDB backend.
 */
@Configuration
@Slf4j
public class BackendDaoConfig {
    @Autowired
    DynamoDbDao dynamoDbDao;
    @Autowired
    SimpleBackendDao simpleBackendDao;

    //This will let us pick between a 'simple' solution and a more concrete one. Also allows for testing application locally
    @Value("${backendType}")
    private String backendType;

    @Bean("BackendDao")
    public ICommitDao getBackendDao() {
        ICommitDao result = null;
        log.trace("getBackendDao - enter");
        if(backendType.equalsIgnoreCase("dynamodb")) {
            result = dynamoDbDao;
        } else {
            result = simpleBackendDao;
        }
        log.trace("getBackendDao - exit");
        return result;
    }
}
