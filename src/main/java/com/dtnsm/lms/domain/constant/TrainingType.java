package com.dtnsm.lms.domain.constant;

/*
    결재유형 :  결재, 확인
 */
public enum TrainingType {
    SELF("Self-training"),
    OTHER("Other");

    private String label;
    TrainingType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
