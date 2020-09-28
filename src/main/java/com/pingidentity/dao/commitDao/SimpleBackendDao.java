package com.pingidentity.dao.commitDao;

import com.amazonaws.util.StringUtils;
import com.pingidentity.objects.GitCommit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Backend is a development resource to help test without a real backend.
 * NOTE: DATA IS NOT PERSISTENT -- IT WILL ALL BE DELETED ON A RESTART
 */
@Component
@Slf4j
public class SimpleBackendDao implements ICommitDao {
    private final Map<String, GitCommit> shaCommitHashMap = new HashMap<>();
    private final Map<String, List<GitCommit>> authorCommitHashMap = new HashMap<>();
    private final Map<String, List<GitCommit>> jiraIdCommitHashMap = new HashMap<>();

    @Override
    public void insert(GitCommit gitCommit) {
        log.debug("insert - enter");
        if(gitCommit == null){
            log.info("GitCommit is null, cannot insert.");
            return;
        }
        if(StringUtils.isNullOrEmpty(gitCommit.getSha1())) {
            log.debug("GitCommit did not have a sha1");
        } else {
            //Sha1 should not have collisions -- so it is just a 1:1.
            shaCommitHashMap.put(gitCommit.getSha1(), gitCommit);
        }
        //Authors
        if(StringUtils.isNullOrEmpty(gitCommit.getAuthor())) {
            log.debug(gitCommit.getSha1() + " - did not contain an author");
        } else {
            String authorKey = gitCommit.getAuthor().toLowerCase();
            if (!authorCommitHashMap.containsKey(authorKey)) {
                authorCommitHashMap.put(authorKey, new ArrayList<>());
            }
            authorCommitHashMap.get(authorKey).add(gitCommit);
        }

        //Jira Ids
        if(gitCommit.getJiraIds() == null || gitCommit.getJiraIds().size() == 0) {
            log.debug(gitCommit.getSha1() + " - did not contain any jiraIds");
        } else {
            for (String jiraId : gitCommit.getJiraIds()) {
                if(StringUtils.isNullOrEmpty(jiraId)) {
                    log.debug("A jira ID was null");
                } else {
                    if (!jiraIdCommitHashMap.containsKey(jiraId)) {
                        jiraIdCommitHashMap.put(jiraId, new ArrayList<>());
                    }
                    jiraIdCommitHashMap.get(jiraId).add(gitCommit);
                }
            }
        }
        log.debug("insert - exit");
    }

    /**
     * This is just a simple implementation -- if we wanted to get crazy, we could put this into a delete queue
     * and process asynchronously as to not slow down the Response.
     * @param commitId
     */
    @Override
    public void delete(String commitId) {
        log.debug("delete - enter");
        //To delete, we need to make sure we delete it from all the lists
        GitCommit gc = shaCommitHashMap.get(commitId);

        if(gc != null) {
            //Remove Authors
            List<GitCommit> authorCommits = authorCommitHashMap.get(gc.getAuthor().toLowerCase());
            removeMatchingFromList(authorCommits, gc);

            //Remove by Jira Ids
            for (String jiraId : gc.getJiraIds()) {
                GitCommit toRemoveJira = null;
                List<GitCommit> jiraCommits = jiraIdCommitHashMap.get(jiraId);
                removeMatchingFromList(jiraCommits, gc);
            }

            // Remove Sha1
            shaCommitHashMap.remove(gc.getSha1());
        }
        log.debug("delete - exit");
    }

    private void removeMatchingFromList(List<GitCommit> commits, GitCommit match) {
        log.trace("removeMatchingFromList - enter");
        GitCommit toRemove = null;
        if(commits != null) {
            for (GitCommit gitCommit : commits) {
                if (gitCommit.getSha1().equals(match.getSha1())) {
                    toRemove = gitCommit;
                    break;
                }
            }
            if (toRemove != null) {
                commits.remove(toRemove);
            }
        }
        log.trace("removeMatchingFromList - exit");
    }

    private Map<String,GitCommit> searchSha1(String sha1) {
        Map<String, GitCommit> results = new HashMap<>();
        if(!StringUtils.isNullOrEmpty(sha1)) {
            GitCommit commit = shaCommitHashMap.get(sha1);
            if(commit != null) {
                results.put(commit.getSha1(),commit);
            }
        }
        return results;
    }

    /**
     * NOTE: If you do not pass in a currentList, then we will check the hashmap
     * @param currentList
     * @param jiraId
     * @return
     */
    private Map<String, GitCommit> searchJiraId(Map<String, GitCommit> currentList, String jiraId) {
        Map<String, GitCommit> results = new HashMap<>();
        if(currentList != null) {
            List<GitCommit> toRemove = new ArrayList<>();
            for (GitCommit gc : currentList.values()) {
                if (!gc.getJiraIds().contains(jiraId)) {
                    toRemove.add(gc);
                }
            }
            for (GitCommit r : toRemove) {
                currentList.remove(r.getSha1());
            }
            results = currentList;
        } else {
            //We can search by the hashmap
            List<GitCommit> jiraList = jiraIdCommitHashMap.get(jiraId);
            if(jiraList != null && jiraList.size() > 0) {
                for(GitCommit g : jiraList) {
                    results.put(g.getSha1(), g);
                }
            }
        }
        return results;
    }
    private Map<String,GitCommit> searchAuthor(Map<String,GitCommit> currentList, String author) {
        Map<String, GitCommit> results = new HashMap<>();
        if(currentList != null) {
            List<GitCommit> toRemove = new ArrayList<>();
            for (GitCommit gc : currentList.values()) {
                if (!gc.getAuthor().equalsIgnoreCase(author)) {
                    log.info("TO REMOVE: " + gc.getAuthor() + " - " + gc.getSha1() + "-" + gc.toString());
                    toRemove.add(gc);
                }
            }
            for (GitCommit r : toRemove) {
                currentList.remove(r.getSha1());
            }
            results = currentList;
        } else {
            //We can search by the hashmap
            List<GitCommit> authorList = authorCommitHashMap.get(author.toLowerCase());
            if(authorList != null && authorList.size() > 0) {
                for(GitCommit g : authorList) {
                    results.put(g.getSha1(),g);
                }
            }
        }
        return results;
    }

    @Override
    public List<GitCommit> search(String sha1, String jiraId, String author) {
        Map<String,GitCommit> results = new HashMap<>();
        boolean hasBeenFilled = false;
        if(!StringUtils.isNullOrEmpty(sha1)) {
            hasBeenFilled = true;
            results = searchSha1(sha1);
        }
        if(!StringUtils.isNullOrEmpty(jiraId)) {
            if(hasBeenFilled) {
                results = searchJiraId(results,jiraId);
            } else {
                hasBeenFilled = true;
                results = searchJiraId(null, jiraId);
            }
        }
        if(!StringUtils.isNullOrEmpty(author)) {
            if(hasBeenFilled) {
                //We have to search within the existing results
                results = searchAuthor(results, author);
            } else {
                results = searchAuthor(null, author);
            }
        }
        List<GitCommit> finalList = new ArrayList<>();
        finalList.addAll(results.values());
        return finalList;
    }

}
