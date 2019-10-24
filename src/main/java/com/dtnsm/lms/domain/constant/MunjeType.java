package com.dtnsm.lms.domain.constant;

public enum MunjeType {
    QUIZ("quiz"),           // 시험
    SURVEY("survey");      // 설문
    private String label;
    MunjeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
