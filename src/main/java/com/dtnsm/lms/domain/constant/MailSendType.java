package com.dtnsm.lms.domain.constant;

public enum MailSendType {
    REQUEST("신청 메일"),           // 신청메일
    APPROVAL1("1차 승인 메일"),     // 1차승인메일
    APPROVAL2("2차 승인 메일");     // 2차승인메일

    private String label;
    MailSendType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
