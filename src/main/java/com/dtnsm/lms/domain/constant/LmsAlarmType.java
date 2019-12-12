package com.dtnsm.lms.domain.constant;

public enum LmsAlarmType {
    Sign("사인 미등록"),
    Manager("관리자 미등록"),
    ParentUser("상위결재권자 미등록");


    private String label;
    LmsAlarmType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
