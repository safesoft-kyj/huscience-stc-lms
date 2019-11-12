package com.dtnsm.lms.domain.constant;

public enum MailTemplateType {
    COURSE_REQUEST("과정 신청 메일"),           // 과정신청 확인 메일
    COURSE_APPR1("과정 신청 메일"),           // 과정신청 확인 메일
    COURSE_APPR2("과정 신청 메일");           // 과정신청 확인 메일

    private String label;
    MailTemplateType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
