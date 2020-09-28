package com.pingidentity.dto;

import com.pingidentity.objects.GitCommit;
import lombok.Data;

import java.util.List;

@Data
public class GitCommitDetailedResponse extends AResponseDto {
    private List<GitCommit> gitCommits;

    public GitCommitDetailedResponse() {

    }
    public GitCommitDetailedResponse(int result, String errorType, String errorDesc) {
        this.setResult(result);
        this.setErrorType(errorType);
        this.setErrorDescription(errorDesc);
    }
}
