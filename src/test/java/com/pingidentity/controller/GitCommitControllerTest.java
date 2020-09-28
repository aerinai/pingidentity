package com.pingidentity.controller;


import com.pingidentity.config.GitCommitConstants;
import com.pingidentity.dto.GitCommitDetailedResponse;
import com.pingidentity.dto.GitCommitJiraResponse;
import com.pingidentity.dto.GitCommitRequest;
import com.pingidentity.facade.GitCommitFacade;
import com.pingidentity.objects.GitCommit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitCommitControllerTest {
    GitCommitController gitCommitController;

    @Mock
    GitCommitFacade mockGitCommitFacade;

    @Before
    public void before() {
        gitCommitController = new GitCommitController(mockGitCommitFacade);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullFacade() {
        new GitCommitController(null);
    }

    @Test
    public void getGitCommit_nullResponse() {
        when(mockGitCommitFacade.getCommit(eq(null),eq(null),eq(null))).thenReturn(null);
        GitCommitDetailedResponse response = gitCommitController.getGitCommit(null,null,null);
        assertNotNull(response);
        assertEquals(GitCommitConstants.ERROR_TYPE_NULL_ERROR, response.getErrorType());
        assertEquals((Integer)GitCommitConstants.RESULT_NULL_ERROR, response.getResult());
    }
    @Test
    public void getGitCommit_exceptionResponse() {
        GitCommitDetailedResponse dto = new GitCommitDetailedResponse();
        dto.setResult(GitCommitConstants.RESULT_OK);
        dto.setGitCommits(new ArrayList<>());
        dto.getGitCommits().add(new GitCommit());
        when(mockGitCommitFacade.getCommit(eq("someSha"),eq(null),eq(null))).thenThrow(new RuntimeException("Something Broke"));
        GitCommitDetailedResponse response = gitCommitController.getGitCommit("someSha",null,null);
        assertNotNull(response);
        assertEquals(GitCommitConstants.ERROR_TYPE_INTERNAL_ERROR, response.getErrorType());
        assertEquals((Integer)GitCommitConstants.RESULT_INTERNAL_ERROR, response.getResult());
    }
    @Test
    public void getGitCommit_successResponse() {
        GitCommitDetailedResponse dto = new GitCommitDetailedResponse();
        dto.setResult(GitCommitConstants.RESULT_OK);
        dto.setGitCommits(new ArrayList<>());
        dto.getGitCommits().add(new GitCommit());
        when(mockGitCommitFacade.getCommit(eq("someSha"),eq(null),eq(null))).thenReturn(dto);
        GitCommitDetailedResponse response = gitCommitController.getGitCommit("someSha",null,null);
        assertNotNull(response);
        assertEquals((Integer)GitCommitConstants.RESULT_OK, response.getResult());
        assertEquals(1, response.getGitCommits().size());
    }

    @Test
    public void insertGitCommit_nullResponse() {
        when(mockGitCommitFacade.insertGitCommit(any(GitCommitRequest.class))).thenReturn(null);
        GitCommitJiraResponse response = gitCommitController.insertGitCommit(new GitCommitRequest());
        assertNotNull(response);
        assertEquals(GitCommitConstants.ERROR_TYPE_NULL_ERROR, response.getErrorType());
        assertEquals((Integer)GitCommitConstants.RESULT_NULL_ERROR, response.getResult());
    }
    @Test
    public void insertGitCommit_exceptionResponse() {
        GitCommitDetailedResponse dto = new GitCommitDetailedResponse();
        dto.setResult(GitCommitConstants.RESULT_OK);
        dto.setGitCommits(new ArrayList<>());
        dto.getGitCommits().add(new GitCommit());
        when(mockGitCommitFacade.insertGitCommit(any(GitCommitRequest.class))).thenThrow(new RuntimeException("Something Broke"));
        GitCommitJiraResponse response = gitCommitController.insertGitCommit(new GitCommitRequest());
        assertNotNull(response);
        assertEquals(GitCommitConstants.ERROR_TYPE_INTERNAL_ERROR, response.getErrorType());
        assertEquals((Integer)GitCommitConstants.RESULT_INTERNAL_ERROR, response.getResult());
    }
    @Test
    public void insertGitCommit_successResponse() {
        GitCommitJiraResponse dto = new GitCommitJiraResponse();
        dto.setResult(GitCommitConstants.RESULT_OK);
        dto.setJiraIds(new ArrayList<>());
        dto.getJiraIds().add("SSD-101");
        when(mockGitCommitFacade.insertGitCommit(any(GitCommitRequest.class))).thenReturn(dto);
        GitCommitJiraResponse response = gitCommitController.insertGitCommit(new GitCommitRequest());
        assertNotNull(response);
        assertEquals((Integer)GitCommitConstants.RESULT_OK, response.getResult());
        assertEquals(1, response.getJiraIds().size());
    }

}
