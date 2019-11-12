package com.dtnsm.lms.domain.constant;

// 시험 합격 여부
public enum QuizStatusType {
    REQUEST ("request"),      // 신청
    ONGOING("ongoing"),      // 진행중
    COMPLETE ("complete"),      // 합격
    FAIL("fail");      // 불합격

    private String label;
    QuizStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
