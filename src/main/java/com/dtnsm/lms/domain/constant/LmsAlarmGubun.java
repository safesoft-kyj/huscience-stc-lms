package com.dtnsm.lms.domain.constant;

public enum LmsAlarmGubun {
    INFO("정보"),       // 정보
    WARNING("경고"),  // 경고
    ERROR("에러");    // 에러

    private String label;
    LmsAlarmGubun(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
