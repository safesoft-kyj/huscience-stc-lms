package com.dtnsm.lms.domain.constant;

public enum BinderAlarmType {
    BINDER_REG("email/binder-reg", "[LMS/%s] Digital Binder 등록 요청", ""),
    BINDER_REVIEW("email/binder-review", "[LMS/%s] Digital Binder 검토 요청", "${empName}, ${inDate}"),
    BINDER_REVIEWED("email/binder-reviewed", "[LMS/%s] Digital Binder 승인 알림", "사용자의 Binder 검토가 완료 되었습니다."),
    //3주,2주,1주,3일,1일
    BINDER_UPDATE("email/binder-update", "[LMS/%s] Digital Binder 정기 검토_업데이트 요청", "${empName}, ${remainDay}"),

    JD_ASSIGNED("email/jd-assign-alert", "[LMS/%s/%s] Job Description 배정 알림", "${jobTitle}"),
    JD_AGREE("email/jd-agree-alert", "[LMS/%s/%s] Job Description 동의 알림", "${jobTitle}, ${empName}"),
    JD_APPROVED("email/jd-approved-alert", "[LMS/%s/%s] Job Description 승인 알림", "Job Description이 승인 되었습니다.");

    private String template;
    private String title;
    private String message;

    BinderAlarmType(String template, String title, String message) {
        this.template = template;
        this.title = title;
        this.message = message;
    }

    public String getTemplate() {
        return template;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
