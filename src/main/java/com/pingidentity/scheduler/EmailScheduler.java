package com.pingidentity.scheduler;

import com.pingidentity.objects.GitCommit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class EmailScheduler {
    //Immutable Objects
    private final List<String> emailAddresses;
    private final String fromAddress;
    private final JavaMailSender javaMailSender;

    //Queue
    private final ConcurrentLinkedQueue<GitCommit> queue = new ConcurrentLinkedQueue<>();

    public EmailScheduler(@Value("${notify.email.addresses}")String[] emailAddresses,
                          @Value("${notify.email.fromAddress}")String fromAddress,
                          JavaMailSender javaMailSender){
        //Allowing email addresses to be empty
        if(emailAddresses != null) {
            this.emailAddresses = Arrays.asList(emailAddresses);
        } else {
            this.emailAddresses = new ArrayList<>();
        }
        this.fromAddress = fromAddress;
        //Requiring from/javaMailSender to be configured
        Assert.notNull(fromAddress, "FromAddress cannot be null");
        Assert.notNull(javaMailSender, "JavaMailSender cannot be null");
        this.javaMailSender = javaMailSender;

    }

    //TODO: We may want to make this configurable, but right now let's just assume 5 minutes is fine
    @Scheduled(fixedDelay = 300000)
    public void runEmailSchedule() {
        log.debug("runEmailSchedul - enter");
        List<GitCommit> gitCommitList= new ArrayList<>();
        while(!queue.isEmpty()){
            gitCommitList.add(queue.poll());
        }
        sendEmail(gitCommitList);
        log.debug("runEmailSchedule - exit");
        //Now that we have the commit lists out of the queue, we can send them off to be emailed.
    }

    /**
     * This will send the emails
     * TODO: we could improve this by validating more of the GitCommit -- even an empty one will render data
     * TODO: We could also improve what information would be given in the email -- talk with the business to determine needs
     * @param commits GitCommit information from datastore
     */
    private void sendEmail(List<GitCommit> commits) {
        SimpleMailMessage message = new SimpleMailMessage();
        if( commits.size() > 0) {
            log.info("Kicking off email");
            if (commits.size() == 1) {
                GitCommit gitCommit = commits.get(0);
                if(gitCommit.getJiraIds() != null) {
                    message.setSubject("Git Commit Notification: " + String.join(",", gitCommit.getJiraIds()));
                } else {
                    message.setSubject("Git Commit Notification: " + gitCommit.getSha1());
                }
                message.setText(gitCommit.getSha1() + " was committed.");
            } else {
                message.setSubject("Git Commits (" + commits.size() + ") Notification");
                //Do a bulk send instead
                boolean isFirst = true;
                for (GitCommit gc : commits) {
                    StringBuilder sb = new StringBuilder();
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(gc.getSha1());
                    message.setText("The following commits have occurred: " + gc.getSha1());
                }
            }
            message.setFrom(fromAddress);
            message.setTo(emailAddresses.toArray(new String[emailAddresses.size()]));
            javaMailSender.send(message);
            log.info("Email sent");
        }
    }

    public void addToQueue(GitCommit gitCommit) {
        queue.add(gitCommit);
    }

}
