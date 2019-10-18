package com.dtnsm.lms.domain.constant;

public enum ApprovalStatusType {
    REQUEST_DONE("request_done"),             // 요청완료
    APPROVAL_TEAM_DONE("approval_team_done"),      // 팀장/부서장 승인 완료
    APPROVAL_MANAGER_DONE("approval_manager_done");    //과정관리자 승인(확인) 완료

    private String label;
    ApprovalStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
