package com.dtnsm.lms.domain.constant;

public enum NotificationGubun {
    Course("미결"),       // 미결
    Document("승인"),  // 승인
    reject("반려");    // 반려

    private String label;
    NotificationGubun(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
