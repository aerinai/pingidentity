package com.pingidentity.scheduler;

import com.amazonaws.services.dynamodbv2.document.Expected;
import com.pingidentity.objects.GitCommit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmailSchedulerTest {
    EmailScheduler emailScheduler;
    @Mock
    JavaMailSender mockJavaMailSender;

    @Before
    public void before() {
        emailScheduler = new EmailScheduler(new String[]{"email@some.com"}, "from@someemail.com", mockJavaMailSender);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullFrom(){
        new EmailScheduler(null, null, mockJavaMailSender);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullSender(){
        new EmailScheduler(null, "some@address.com", null);
    }

    @Test
    public void runEmailSchedule_empty() {
        emailScheduler.runEmailSchedule();
        verify(mockJavaMailSender,times(0)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void runEmailSchedule_withQueueSingle() {
        emailScheduler.addToQueue(new GitCommit());
        emailScheduler.runEmailSchedule();
        verify(mockJavaMailSender,times(1)).send(any(SimpleMailMessage.class));
    }
    @Test
    public void runEmailSchedule_withQueueMultiple() {
        emailScheduler.addToQueue(new GitCommit());
        emailScheduler.addToQueue(new GitCommit());
        emailScheduler.runEmailSchedule();
        verify(mockJavaMailSender,times(1)).send(any(SimpleMailMessage.class));
    }
}
