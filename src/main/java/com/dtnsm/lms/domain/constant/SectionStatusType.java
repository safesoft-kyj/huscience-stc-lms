package com.dtnsm.lms.domain.constant;

// 시험 합격 여부
public enum SectionStatusType {
    COMPLETE ("complete"),      // 강의완료
    REQUEST ("request"),      // 요청
    ONGOING("ongoing");      // 진행중

    private String label;
    SectionStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
