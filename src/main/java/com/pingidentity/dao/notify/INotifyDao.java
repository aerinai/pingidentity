package com.pingidentity.dao.notify;

import com.pingidentity.objects.GitCommit;

public interface INotifyDao {
    void notify(GitCommit gitCommit);
}
