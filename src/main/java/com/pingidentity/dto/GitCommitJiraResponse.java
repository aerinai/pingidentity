package com.pingidentity.dto;

import lombok.Data;

import java.util.List;

@Data
public class GitCommitJiraResponse extends AResponseDto {
    List<String> jiraIds;
    public GitCommitJiraResponse() {

    }
    public GitCommitJiraResponse(int result, String errorType, String errorDesc) {
        this.setResult(result);
        this.setErrorType(errorType);
        this.setErrorDescription(errorDesc);
    }
}
