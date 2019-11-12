package com.dtnsm.lms.domain.constant;

// 시험 합격 여부
public enum SurveyStatusType {
    COMPLETE ("complete"),      // 완료
    ONGOING("ongoing"),      // 진행중
    REQUEST("request");      // 요청

    private String label;
    SurveyStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
