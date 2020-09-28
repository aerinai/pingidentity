package com.pingidentity.dao.notify;

import com.pingidentity.objects.GitCommit;
import com.pingidentity.scheduler.EmailScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Due to how long it takes for SMTP to actually send, we are just writing this stuff to a queue
 * so that we do not slow down the response.
 */
@Component
@Slf4j
public class EmailNotifyDao implements INotifyDao {
    //Immutable Objects
    private final EmailScheduler emailScheduler;

    public EmailNotifyDao(EmailScheduler emailScheduler) {
        Assert.notNull(emailScheduler,"EmailScheduler is null");
        this.emailScheduler = emailScheduler;
    }

    @Override
    public void notify(GitCommit gitCommit) {
        if(gitCommit == null) {
            log.warn("GitCommit was null - not adding to Email Queue");
        } else {
            log.info("ADDING COMMIT TO QUEUE");
            try {
                emailScheduler.addToQueue(gitCommit);
            } catch (Exception ex) {
                log.error("Unable to add Notification to queue: " + gitCommit.getSha1());
            }
        }
    }
}
