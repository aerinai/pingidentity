package com.pingidentity.dao.notifyDao;

import com.pingidentity.dao.notify.EmailNotifyDao;
import com.pingidentity.dto.GitCommitRequest;
import com.pingidentity.objects.GitCommit;
import com.pingidentity.scheduler.EmailScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailNotifyDaoTest {
    EmailNotifyDao emailNotifyDao;
    @Mock
    EmailScheduler mockEmailScheduler;

    @Before
    public void before() {
        emailNotifyDao = new EmailNotifyDao(mockEmailScheduler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullScheduler() {
        new EmailNotifyDao(null);
    }

    @Test
    public void notify_nullTest() {
        emailNotifyDao.notify(null);
    }
    @Test
    public void notify_emptyTest() {
        emailNotifyDao.notify(new GitCommit());
    }
    @Test
    public void notify_errorTest() {
        doThrow(new RuntimeException("Something Broke")).when(mockEmailScheduler).addToQueue(any(GitCommit.class));
        emailNotifyDao.notify(new GitCommit());
    }
    @Test
    public void notify_validTest() {
        GitCommit gitCommit = new GitCommit();
        gitCommit.setAuthor("author");
        gitCommit.setDate(new Date());
        gitCommit.setMessage("message");
        gitCommit.setSha1("sha1");
        emailNotifyDao.notify(gitCommit);
    }

}