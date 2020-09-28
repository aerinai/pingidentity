package com.pingidentity.dao.notify;

import com.pingidentity.objects.GitCommit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogNotifyDao implements INotifyDao {
    @Override
    public void notify(GitCommit gitCommit) {
        if(gitCommit != null) {
            //TODO: Discuss with business owners / Devs on exactly what information is pertinent
            log.info("GIT COMMIT OCCURRED: " + gitCommit.toString());
        } else {
            log.warn("GIT COMMIT OCCURRED: COMMIT WAS NULL");
        }
    }
}
