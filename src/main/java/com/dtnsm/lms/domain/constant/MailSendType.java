package com.dtnsm.lms.domain.constant;

public enum MailSendType {
    ACCOUNT_ASSIGN("필수교육대상자지정"),    // 필수교육대상자 지정 메일
    REQUEST("신청 메일"),           // 신청메일
    APPROVAL("승인 메일"),     // 승인메일
    REJECT("기각 메일");     // 기각 메일

    private String label;
    MailSendType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
