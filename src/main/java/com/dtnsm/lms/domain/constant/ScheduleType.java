package com.dtnsm.lms.domain.constant;

public enum ScheduleType {
    CALENDAR("calendar"),
    MATRIX("matrix");

    private String label;
    ScheduleType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
