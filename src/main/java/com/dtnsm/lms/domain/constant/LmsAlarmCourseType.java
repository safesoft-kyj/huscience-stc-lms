package com.dtnsm.lms.domain.constant;

public enum LmsAlarmCourseType {
    CourseAccountAssign("교육수강생 지정"),    // 필수교육대상자 지정 메일
    CourseReportApproach("보고서 작성기간 임박"),          // 외부교육참석보고서(교육일 이후 3일이내) 임박 메일
    CourseToDateApproach("교육종료일 임박"),               // 교육종료일 임박 메일
    CourseToDateExpire("교육기간 종료"),               // 교육기간 종료 메일
    Request("수강생이 직접 교육신청한후 경우 당사자에게 메일"),
    Approval("수강생이 직접 교육신청한후 팀장에게 보내지는 메일"),
    ApprovalTeam("팀장 승인후 관리자에게 보내지는 메일"),     // 팀장 승인 -> 관리자에게 메시지
    ApprovalManager("관리자 승인후 신청자에게 보내지는 메일 "),     // 관리자 승인 -> 신청자(기안자)에게 메시지
    Reject("기각시 신청자에게 보내지는 메일"),
    DocApproval("교육참석보고서 작성후 팀장에게 보내지는 메일"),
    DocApprovalTeam("교육참석보고서 팀장 승인후 기안자&관리자에게 보내지는 메일"),
    DocApprovalManger("교육참석보고서 관리자 승인후 기안자에게 보내지는 메일"),
    DocReject("교육참석보고서 기각시 기안자에게 보내지는 메일")
    ;

    private String label;
    LmsAlarmCourseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
