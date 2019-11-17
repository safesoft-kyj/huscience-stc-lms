package com.dtnsm.lms.domain.constant;

public enum ApprovalStatus {
    none("미결"),       // 미결
    approval("승인"),  // 승인
    reject("반려");    // 반려

    private String label;
    ApprovalStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
