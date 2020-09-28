package com.pingidentity.dao.commitDao;

import com.pingidentity.objects.GitCommit;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class SimpleBackendDaoTest {
    SimpleBackendDao simpleBackendDao;

    @Before
    public void before() {
        simpleBackendDao = new SimpleBackendDao();
        fillBackendDao();
    }
    @Test
    public void insert_nullTest() {
        simpleBackendDao.insert(null);
    }
    @Test
    public void insert_emptyCommitTest() {
        simpleBackendDao.insert(new GitCommit());
    }
    @Test
    public void insert_withSha1() {
        GitCommit gc = new GitCommit();
        gc.setSha1("sha1");
        simpleBackendDao.insert(gc);
    }
    @Test
    public void insert_withAuthor() {
        GitCommit gc = new GitCommit();
        gc.setAuthor("author");
        simpleBackendDao.insert(gc);
    }
    @Test
    public void insert_withJiraId() {
        GitCommit gc = new GitCommit();
        gc.setJiraIds(new ArrayList<>());
        gc.getJiraIds().add("SSD-101");
        simpleBackendDao.insert(gc);
    }
    @Test
    public void delete_nullTest() {
        simpleBackendDao.delete(null);
    }
    @Test
    public void delete_notExistTest() {
        simpleBackendDao.delete("NotARealSha");
    }
    @Test
    public void delete_validTest() {
        assertEquals(1,simpleBackendDao.search(SHA1_1,null,null).size());
        assertEquals(1,simpleBackendDao.search(null,"XYZ-0",null).size());
        assertEquals(1,simpleBackendDao.search(null,null, "author A").size());
        simpleBackendDao.delete(SHA1_1);
        assertEquals(0,simpleBackendDao.search(SHA1_1,null,null).size());
        assertEquals(0,simpleBackendDao.search(null,"XYZ-0",null).size());
        assertEquals(0,simpleBackendDao.search(null,null,"author A").size());
    }

    @Test
    public void search_notFound() {
        assertEquals(0,simpleBackendDao.search("NOTFOUND",null,null).size());
        assertEquals(0,simpleBackendDao.search(null,"NOT-101",null).size());
        assertEquals(0,simpleBackendDao.search(null,null,"FAKE AUTH").size());
    }
    @Test
    public void search_foundSimple() {
        assertEquals(1,simpleBackendDao.search(SHA1_2,null,null).size());
        assertEquals(1,simpleBackendDao.search(null,"XYZ-0",null).size());
        assertEquals(1,simpleBackendDao.search(null,null,"author a").size());
    }
    @Test
    public void search_foundComplex() {
        assertEquals(1,simpleBackendDao.search(SHA1_1,"ABC-1","author A").size());
        assertEquals(0,simpleBackendDao.search(SHA1_1,"XYZ-22","author A").size());
        assertEquals(0,simpleBackendDao.search(SHA1_1,"ABC-1","author B").size());
    }
    @Test
    public void search_findMultiple() {
        assertEquals(3,simpleBackendDao.search(null,"ABC-1",null).size());
    }

    private final String SHA1_1 = "743803DE635CBCC9001A61AB8498261B1ACB3E83";
    private final String SHA1_2 = "5D4AC0BF04A83A39619F76203BB2D7C08BA7501C";
    private final String SHA1_3 = "1F9161437F1CDD9BADAF4D3B0C0F07D64D4F5B4C";
    private void fillBackendDao() {
        simpleBackendDao.insert(createGitCommit(SHA1_1,"SSD-101","author A", "SSD-101","SSD-11","XYZ-0","ABC-1"));
        simpleBackendDao.insert(createGitCommit(SHA1_2,"Another Message","author B","SSD-101","XYZ-1","ABC-1"));
        simpleBackendDao.insert(createGitCommit(SHA1_3,"Third Message","author C","SSD-11","XYZ-22","ABC-1"));
    }
    private GitCommit createGitCommit(String sha1, String message, String author, String... jiraIds) {
        GitCommit gitCommit = new GitCommit();
        gitCommit.setAuthor(author);
        gitCommit.setSha1(sha1);
        gitCommit.setMessage(message);
        gitCommit.setJiraIds(new ArrayList<>());
        if(jiraIds != null) {
            for(String s : jiraIds) {
                gitCommit.getJiraIds().add(s);
            }
        }
        return gitCommit;
    }

}
