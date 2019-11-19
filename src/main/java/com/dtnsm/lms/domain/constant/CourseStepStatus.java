package com.dtnsm.lms.domain.constant;

public enum CourseStepStatus {
    none("none"),
    request("신청중"),
    process("교육중"),
    complete("종료");

    private String label;
    CourseStepStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
