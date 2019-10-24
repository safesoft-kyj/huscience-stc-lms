package com.dtnsm.lms.domain.constant;

public enum MunjeGubun {
    SINGLE("single"),           // 주관식
    MULTIPLE("multiple");      // 객관식
    private String label;
    MunjeGubun(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
