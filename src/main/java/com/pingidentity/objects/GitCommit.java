package com.pingidentity.objects;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * A domain object representing a git commit message. The intent is
 * for another program to provide these values by parsing a git repository. For
 * your purposes, you will initialize them in your tests (See AppTest.java)
 */
@Data
@Slf4j
public class GitCommit
{
    private String message;
    private String sha1;
    private String author;
    private Date date;
    //This will not be passed in, only created before DB insertion
    private List<String> jiraIds;

}
