package com.pingidentity.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingidentity.config.GitCommitConstants;
import com.pingidentity.dto.GitCommitDetailedResponse;
import com.pingidentity.dto.GitCommitRequest;
import com.pingidentity.dto.GitCommitJiraResponse;
import com.pingidentity.facade.GitCommitFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * Holds the REST API that we will be using for the application
 */
@RestController
@Slf4j
public class GitCommitController {
    //Immutable Objects
    private final GitCommitFacade gitCommitFacade;

    public GitCommitController(GitCommitFacade gitCommitFacade) {
        Assert.notNull(gitCommitFacade,"GitCommitFacade is null");
        this.gitCommitFacade = gitCommitFacade;
    }

    /**
     * Will grab the detailed response of the GitCommit data that is stored
     * @param sha1 (optional) can be used to search by the sha1 of the GitCommit
     * @param jiraId (optional) can be used to search for the jiraId
     * @param author (optional) can be used to search by the author
     * @return a list of GitCommits that meet the criteria that was passed in. Max return of 10
     */
    @RequestMapping(value = "/gitCommit", method = RequestMethod.GET)
    public GitCommitDetailedResponse getGitCommit(@RequestParam(value = "sha1", required = false) String sha1,
                                               @RequestParam(value = "jiraId", required = false) String jiraId,
                                               @RequestParam(value = "author", required = false) String author) {
        log.info("GET gitCommit {} - {} - {} - Enter", sha1, jiraId, author);
        GitCommitDetailedResponse response = null;
        try {
            response = gitCommitFacade.getCommit(sha1, jiraId, author);
            if(response == null) {
                response = new GitCommitDetailedResponse(GitCommitConstants.RESULT_NULL_ERROR, GitCommitConstants.ERROR_TYPE_NULL_ERROR, "gitCommitFacade returned a null object.");
            }
        } catch (Exception ex) {
            log.error("POST - gitCommit threw an exception: {}", ex.getMessage(), ex);
            response = new GitCommitDetailedResponse(GitCommitConstants.RESULT_INTERNAL_ERROR, GitCommitConstants.ERROR_TYPE_INTERNAL_ERROR, ex.getMessage());
        }
        log.info("GET gitCommit - Exit");
        return response;
    }

    /**
     * An external application will be writing to this API.
     * @param request A GitCommit object (without JiraIds)
     * @return a list of Jira Ids that were taken out of the message
     */
    @RequestMapping(value = "/gitCommit", method = RequestMethod.POST)
    public GitCommitJiraResponse insertGitCommit(@RequestBody GitCommitRequest request) {
        log.info("POST gitCommit - Enter");
        GitCommitJiraResponse response = null;
        try {
            response = gitCommitFacade.insertGitCommit(request);
            if(response == null) {
                response = new GitCommitJiraResponse(GitCommitConstants.RESULT_NULL_ERROR, GitCommitConstants.ERROR_TYPE_NULL_ERROR, "gitCommitFacade returned a null object.");
            }
        } catch (Exception ex){
            log.error("POST - gitCommit threw an exception: {}", ex.getMessage(), ex);
            response = new GitCommitJiraResponse(GitCommitConstants.RESULT_INTERNAL_ERROR, GitCommitConstants.ERROR_TYPE_INTERNAL_ERROR, ex.getMessage());
        }
        log.info("POST gitCommit - Exit");
        return response;
    }
}