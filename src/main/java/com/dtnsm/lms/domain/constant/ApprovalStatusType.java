package com.dtnsm.lms.domain.constant;

public enum ApprovalStatusType {
    REQUEST_DONE("request_done"),             // 요청완료
    APPROVAL_TEAM_DONE("approval_team_done"),      // 팀장/부서장 승인 처리
    APPROVAL_TEAM_REJECT("approval_team_reject"),      // 팀장/부서장 기각 처리
    APPROVAL_MANAGER_DONE("approval_manager_done"),    //과정관리자 승인 처리
    APPROVAL_MANAGER_REJECT("approval_manager_reject"),    //과정관리자 기각 완료
    APPROVAL_COMPLETE("approval_complete");             //교육완료

    private String label;
    ApprovalStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
