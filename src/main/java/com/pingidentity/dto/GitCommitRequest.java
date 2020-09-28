package com.pingidentity.dto;

import com.pingidentity.objects.GitCommit;
import lombok.Data;

@Data
public class GitCommitRequest extends GitCommit {
    //Wrapper class in case we need to add logic here that is specific to the incoming object
}
