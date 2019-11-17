package com.dtnsm.lms.domain.constant;

public enum CourseStepStatus {
    none("none"),
    request("신청"),
    section("강의"),
    quiz("시험"),
    survey("설문"),
    complete("종료");

    private String label;
    CourseStepStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
