package com.dtnsm.lms.domain.constant;

public enum CourseStepStatus {
    none("신청대기"),
    request("신청중"),
//    periodWait("교육대기"),
    process("교육중"),
    periodEnd("기간만료"),
    wait("참석등록중"),
    reject("반려"),
    complete("종료");

    private String label;
    CourseStepStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
