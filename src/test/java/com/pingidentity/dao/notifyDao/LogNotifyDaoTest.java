package com.pingidentity.dao.notifyDao;

import com.pingidentity.dao.notify.LogNotifyDao;
import com.pingidentity.objects.GitCommit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class LogNotifyDaoTest {
    LogNotifyDao logNotifyDao;
    @Before
    public void before() {
        logNotifyDao = new LogNotifyDao();
    }
    @Test
    public void notify_nullTest() {
        logNotifyDao.notify(null);
    }
    @Test
    public void notify_emptyTest() {
        logNotifyDao.notify(new GitCommit());
    }
    @Test
    public void notify_validTest() {
        GitCommit gitCommit = new GitCommit();
        gitCommit.setAuthor("author");
        gitCommit.setDate(new Date());
        gitCommit.setMessage("message");
        gitCommit.setSha1("sha1");
        logNotifyDao.notify(gitCommit);
    }
}
