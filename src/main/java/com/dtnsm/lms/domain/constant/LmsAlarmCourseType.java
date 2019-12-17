package com.dtnsm.lms.domain.constant;

public enum LmsAlarmCourseType {
    CourseAccountAssign("교육수강생 지정"),    // 필수교육대상자 지정 메일
    CourseReportApproach("보고서 작성기간 임박"),          // 외부교육참석보고서(교육일 이후 3일이내) 임박 메일
    CourseToDateApproach("교육종료일 임박"),               // 교육종료일 임박 메일
    Request("신청 메일"),           // 신청메일
    Approval("승인 메일"),     // 1차승인메일
    Reject("기각 메일");     // 기각 메일


    private String label;
    LmsAlarmCourseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
