package com.dtnsm.lms.domain.constant;

/*
    결재유형 :  결재, 확인
 */
public enum DocType {
    none("미진행"),
    appr("결재"),
    check("확인");

    private String label;
    DocType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
