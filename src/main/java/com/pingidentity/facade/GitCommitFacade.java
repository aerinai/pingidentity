package com.pingidentity.facade;
import com.amazonaws.util.StringUtils;
import com.pingidentity.dao.commitDao.ICommitDao;
import com.pingidentity.dao.notify.INotifyDao;
import com.pingidentity.objects.GitCommit;
import com.pingidentity.config.GitCommitConstants;
import com.pingidentity.dto.GitCommitDetailedResponse;
import com.pingidentity.dto.GitCommitRequest;
import com.pingidentity.dto.GitCommitJiraResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class GitCommitFacade {

    //Immutable Objects
    private final List<String> jiraIdStyles;
    private final ICommitDao backendDao;
    private final List<INotifyDao> notificationList;

    //Some constant for number regex
    private final String NUMBER_REGEX = "d+";

    public GitCommitFacade(@Value("${jira.ids}") String[] jiraIdStyles, @Qualifier("BackendDao") ICommitDao backendDao,
                           @Qualifier("NotificationList") List<INotifyDao> notificationList) {
        this.jiraIdStyles = Arrays.asList(jiraIdStyles);
        this.backendDao = backendDao;
        this.notificationList = notificationList;
        log.info("Notification List: " + notificationList.size());
    }

    //Get Jira IDs from Configuration
    public GitCommitJiraResponse insertGitCommit(GitCommitRequest request) {
        GitCommitJiraResponse response = new GitCommitJiraResponse();
        response.setResult(GitCommitConstants.RESULT_OK);
        if(isValid(request)) {
            //Now we have to parse the message for a Jira ID
            List<String> allJiraIds = findJiraIds(request.getMessage());
            log.info("Jira IDs found: " + allJiraIds.size());
            request.setJiraIds(allJiraIds);
            //Insert it into the db
            backendDao.insert(request);
            doNotifications(request);
            response.setJiraIds(allJiraIds);
        } else {
            response.setResult(GitCommitConstants.RESULT_INVALID_REQUEST);
            response.setErrorType(GitCommitConstants.ERROR_TYPE_INVALID_REQUEST);
            response.setErrorDescription("Unable to parse request -- please check for validity");
        }
        return response;
    }
    public void doNotifications(GitCommit commitRequest) {
        if(notificationList != null) {
                for (INotifyDao dao : notificationList) {
                    if(dao != null) {
                        try {
                            log.info("DAO: " + dao.getClass());
                            dao.notify(commitRequest);
                        }catch (Exception ex) {
                            log.error("Exception: {}", ex.getMessage(), ex);
                        }
                    }
                }
        }
    }
    protected List<String> findJiraIds(String message) {
        List<String> foundJiraIds = new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(message)) {
            //We are making the assumption all JIRA ids are a set of letters, followed by a dash, followed by numbers.
            //e.g. SSD-123, XY-33210, AAA-2
            log.info("Jira Styles: " + jiraIdStyles);
            for (String style : jiraIdStyles) {
                log.info("Style: " + style);
                Pattern p = Pattern.compile(style + "-\\d+");
                Matcher m = p.matcher(message);
                while (m.find()) {
                    String jiraId = m.group();
                    //Get rid of duplicates
                    if (!foundJiraIds.contains(jiraId)) {
                        foundJiraIds.add(jiraId);
                    }
                }
            }
        }
        return foundJiraIds;
    }

    private boolean isValid(GitCommitRequest request) {
        //Make some assumptions about what is valid input
        boolean isValid = true;
        if(request == null) {
            isValid = false;
        } else if(!isValidSHA1(request.getSha1())){
            isValid = false;
        }
        return isValid;
    }
    private boolean isValidSHA1(String s) {
        boolean result = false;
        if(!StringUtils.isNullOrEmpty(s)) {
            result = s.matches("^[a-fA-F0-9]{40}$");
        }
        return result;
    }

    public GitCommitDetailedResponse getCommit(String sha1, String jiraId, String author) {
        List<GitCommit> results = null;
        GitCommitDetailedResponse r = new GitCommitDetailedResponse();
        try {
            results = backendDao.search(sha1, jiraId, author);
            r.setGitCommits(results);
            r.setResult(GitCommitConstants.RESULT_OK);
        }catch (Exception ex) {
            log.error("GetCommit Failed: {}", ex.getMessage(), ex);
            r.setResult(GitCommitConstants.RESULT_INTERNAL_ERROR);
            r.setErrorType(GitCommitConstants.ERROR_TYPE_INTERNAL_ERROR);
            r.setErrorDescription("GetCommit Failed: " + ex.getMessage());
        }
        return r;
    }
}
