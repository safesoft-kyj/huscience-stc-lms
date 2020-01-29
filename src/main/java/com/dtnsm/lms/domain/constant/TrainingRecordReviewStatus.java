package com.dtnsm.lms.domain.constant;

public enum TrainingRecordReviewStatus {
    REQUEST("요청중", "info"), REVIEWED("검토완료", "success"), REJECTED("반려", "danger");

    private String label;
    private String className;
    TrainingRecordReviewStatus(String label, String className) {
        this.label = label;
        this.className = className;
    }

    public String getLabel() {
        return label;
    }

    public String getClassName() {
        return className;
    }
}
