package com.pingidentity.facade;

import com.pingidentity.config.GitCommitConstants;
import com.pingidentity.dao.commitDao.SimpleBackendDao;
import com.pingidentity.dao.notify.EmailNotifyDao;
import com.pingidentity.dao.notify.INotifyDao;
import com.pingidentity.dao.notify.LogNotifyDao;
import com.pingidentity.dto.GitCommitDetailedResponse;
import com.pingidentity.dto.GitCommitJiraResponse;
import com.pingidentity.dto.GitCommitRequest;
import com.pingidentity.objects.GitCommit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GitCommitFacadeTest {
    GitCommitFacade gitCommitFacade;

    @Mock
    EmailNotifyDao emailNotifyDao;
    @Mock
    LogNotifyDao logNotifyDao;
    @Mock
    SimpleBackendDao mockSimpleBackendDao;
    @Before
    public void before() {
        gitCommitFacade = new GitCommitFacade(new String[]{"SSD"}, mockSimpleBackendDao, new ArrayList<>());
    }

    @Test
    public void findJiraIds() {
        List<String> ids = gitCommitFacade.findJiraIds("A message with a jira SSD-110");
        assertTrue(ids.contains("SSD-110"));
        ids = gitCommitFacade.findJiraIds("SSD-6 was not a the problem, SSD-3356 was.");
        //Proves we got rid of this
        assertFalse(ids.contains("SSD-110"));
        //Proves both of these were found
        assertTrue(ids.contains("SSD-3356"));
        assertTrue(ids.contains("SSD-6"));
    }
    @Test
    public void doNotifications_nullTest() {
        gitCommitFacade.doNotifications(null);
    }
    @Test
    public void doNotifications_noNotificationsTest() {
        gitCommitFacade.doNotifications(new GitCommit());
    }
    @Test
    public void doNotifications_nullNotificationsTest() {
        List<INotifyDao> daoList = new ArrayList<>();
        daoList.add(null);
        gitCommitFacade = new GitCommitFacade(new String[]{"SSD"},new SimpleBackendDao(), daoList);
        gitCommitFacade.doNotifications(new GitCommit());
    }
    @Test
    public void doNotifications_realNotificationsTest() {
        List<INotifyDao> daoList = new ArrayList<>();
        daoList.add(emailNotifyDao);
        gitCommitFacade = new GitCommitFacade(new String[]{"SSD"},new SimpleBackendDao(), daoList);
        gitCommitFacade.doNotifications(new GitCommit());
        verify(emailNotifyDao,times(1)).notify(any(GitCommit.class));
    }
    @Test
    public void doNotifications_errorNotificationsTest() {
        List<INotifyDao> daoList = new ArrayList<>();
        daoList.add(emailNotifyDao);
        daoList.add(logNotifyDao);
        doThrow(new RuntimeException("Something Broke")).when(emailNotifyDao).notify(any(GitCommit.class));
        gitCommitFacade = new GitCommitFacade(new String[]{"SSD"},new SimpleBackendDao(), daoList);
        gitCommitFacade.doNotifications(new GitCommit());
        verify(emailNotifyDao,times(1)).notify(any(GitCommit.class));
        //verifies that even if email had an exception log, was still executed
        verify(logNotifyDao,times(1)).notify(any(GitCommit.class));
    }
    @Test
    public void insertGitCommit_invalid() {
        GitCommitJiraResponse response = gitCommitFacade.insertGitCommit(null);
        assertEquals((Integer)GitCommitConstants.RESULT_INVALID_REQUEST, response.getResult());
        response = gitCommitFacade.insertGitCommit(new GitCommitRequest());
        assertEquals((Integer)GitCommitConstants.RESULT_INVALID_REQUEST, response.getResult());
        GitCommitRequest r = new GitCommitRequest();
        r.setSha1("notARealSha");
        response = gitCommitFacade.insertGitCommit(r);
        assertEquals((Integer)GitCommitConstants.RESULT_INVALID_REQUEST, response.getResult());
    }
    @Test
    public void insertGitCommit_valid() {
        GitCommitRequest r = new GitCommitRequest();
        r.setSha1("743803DE635CBCC9001A61AB8498261B1ACB3E83");
        GitCommitJiraResponse response = gitCommitFacade.insertGitCommit(r);
        assertEquals((Integer)GitCommitConstants.RESULT_OK, response.getResult());
    }
    @Test
    public void insertGitCommit_complete() {
        GitCommitRequest r = new GitCommitRequest();
        r.setSha1("743803DE635CBCC9001A61AB8498261B1ACB3E83");
        r.setMessage("SSD-101 and SSD-202 are without data. Don't forget SSD-111,SSD-234, and SSD-3436.");
        r.setAuthor("Author");
        r.setDate(new Date());
        GitCommitJiraResponse response = gitCommitFacade.insertGitCommit(r);
        assertEquals((Integer)GitCommitConstants.RESULT_OK, response.getResult());
        assertTrue(response.getJiraIds().contains("SSD-101"));
        assertTrue(response.getJiraIds().contains("SSD-202"));
        assertTrue(response.getJiraIds().contains("SSD-111"));
        assertTrue(response.getJiraIds().contains("SSD-234"));
        assertTrue(response.getJiraIds().contains("SSD-3436"));
        assertEquals(5, response.getJiraIds().size());
    }
    @Test
    public void getGitCommit_successTest() {
        List<GitCommit> list = new ArrayList<>();
        list.add(new GitCommit());
        when(mockSimpleBackendDao.search(eq(null),eq(null),eq(null))).thenReturn(list);
        GitCommitDetailedResponse r = gitCommitFacade.getCommit(null,null,null);
        assertEquals((Integer)GitCommitConstants.RESULT_OK, r.getResult());
        assertEquals(1, r.getGitCommits().size());
    }
    @Test
    public void getGitCommit_errorTest() {
        List<GitCommit> list = new ArrayList<>();
        list.add(new GitCommit());
        when(mockSimpleBackendDao.search(eq(null),eq(null),eq(null))).thenThrow(new RuntimeException("Something Broke"));
        GitCommitDetailedResponse r = gitCommitFacade.getCommit(null,null,null);
        assertEquals((Integer)GitCommitConstants.RESULT_INTERNAL_ERROR, r.getResult());
        assertEquals(GitCommitConstants.ERROR_TYPE_INTERNAL_ERROR, r.getErrorType());
    }
}
