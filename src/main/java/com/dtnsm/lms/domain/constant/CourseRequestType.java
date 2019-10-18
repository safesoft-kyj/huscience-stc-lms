package com.dtnsm.lms.domain.constant;

// 교육 신청 유형
public enum CourseRequestType {
    SPECIFY ("1"),      // 교육대상자 관리자 지정
    REQUEST("2");      // 사용자 신청

    private String label;
    CourseRequestType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
