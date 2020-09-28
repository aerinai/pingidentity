package com.pingidentity.dao.commitDao;

import com.pingidentity.objects.GitCommit;

import java.util.List;

public interface ICommitDao {
    void insert(GitCommit gitCommit);
    void delete(String commitId);
    List<GitCommit> search(String sha1, String jiraId, String author);

}
