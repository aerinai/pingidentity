package com.pingidentity.config;

import com.pingidentity.dao.notify.EmailNotifyDao;
import com.pingidentity.dao.notify.INotifyDao;
import com.pingidentity.dao.notify.LogNotifyDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class NotifyDaoConfig {
    @Autowired
    EmailNotifyDao emailNotifyDao;
    @Autowired
    LogNotifyDao logNotifyDao;

    @Value("${notify.email.enabled}")
    boolean isEmailNotifyEnabled;
    @Value("${notify.log.enabled}")
    boolean isLogNotifyEnabled;

    @Bean("NotificationList")
    public List<INotifyDao> getNotificationList() {
        log.trace("getNotificationList - enter");
        List<INotifyDao> notifyDaoList = new ArrayList<>();
        if(isEmailNotifyEnabled) {
            log.info("Notify By Email has been Added");
            notifyDaoList.add(emailNotifyDao);
        }
        if(isLogNotifyEnabled) {
            log.info("Notify By Log");
            notifyDaoList.add(logNotifyDao);
        }
        log.trace("getNotificationList - exit");
        return notifyDaoList;
    }
}
