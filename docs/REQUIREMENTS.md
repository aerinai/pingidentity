
# DevOps Engineer Mt Fuji

This is a take-home programming assignment as part of the Ping Identity interview
process. Feel free to clone this repo, but _please_ delete any submitted answers after the interview, so that we can re-use the problem.

## Problem:

As DevOps engineer, you are building the pipeline out to production. Ping's
Support group has asked to be notified when a pipeline starts, and wants to
have its JIRA issue tracking system updated.

When the Pipeline starts, you'll have access to a set of git commits,
containing a commit message, sha, authorship. An example git commit:

```
commit 55c0bb88b6e4f096574991dd9217bcf8c745d05e
Author: Example User <example@pingidentity.com>
Date:   Mon May 4 17:09:19 2015 -0600

    SSD-101 super duper feature
    Fix tomcat issue with using forks over spoons.
```

These git commits will be provided as a `GitCommit` object by another program.

Your assignment is to write a method which returns 'SSD-101' given this input, and a general solution to return all of the tickets present in any git commits provided.

Basically, implement this function: [App#getJiraTickets()](https://github.com/dalvizu/devops-fuji/blob/master/src/main/java/com/pingidentity/App.java#L15)

_Note_: The string 'SSD-101' is the 'key' of a JIRA issue. JIRA is a popular ticketing system in software development and common in the open source community. The key of an issue is its project name (`SSD`) followed by a hyphen and then a number (`101`). For more information on what an issue is, see [what is an issue?](https://confluence.atlassian.com/jira064/what-is-an-issue-720416138.html)

This method and the supporting domain objects have been provided in the src folder, which follows a [standard maven layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).

If you have any questions, feel free to ask through your Ping contact. You'll be asked to present your solution to an interview panel of engineers and argue for any decisions you're making, so be prepared to discuss your solution. Remember to document any assumptions you may be making.

## Submitting:

To submit your answer, open a merge request, or simply zip up the archive and send them back to your Ping contact.

## Required Tools
* git
* java
* maven (https://maven.apache.org/install.html)
* junit
