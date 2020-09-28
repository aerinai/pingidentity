package com.pingidentity.dao.commitDao;

import com.pingidentity.objects.GitCommit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This will deal with CRUD for gitCommit object to/from DynamoDB
 */
@Component
@Slf4j
public class DynamoDbDao implements ICommitDao{
    public void insert(GitCommit gitCommit) {
        log.warn("insert - NOT YET CODED");
        //TODO: Make this work
    }
    public void delete(String commitId) {
        log.warn("insert - NOT YET CODED");
        //TODO: Make this work;
    }

    @Override
    public List<GitCommit> search(String sha1, String jiraId, String author) {
        return null;
    }

}
