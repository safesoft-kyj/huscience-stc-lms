package com.dtnsm.lms.domain.constant;

public enum BinderAlarmType {
    BINDER_REVIEW("email/email-template", "[LMS/Binder/%s] Binder 검토 요청 알림", "사용자의 Binder를 확인해 주세요."),
    BINDER_REVIEWED("email/email-template", "[LMS/Binder/%s] Binder 검토 요청 완료", "사용자의 Binder 검토가 완료 되었습니다."),
    BINDER_UPDATE("email/email-template", "[LMS/Binder/%s] Binder 업데이트 요청 완료", "최신 정보로 Binder 업데이트를 진행 후 매니저에게 검토 요청을 진행해 주세요."),
    JD_ASSIGNED("email/email-template", "[LMS/Binder/%s] Job Description 배정 알림", "Job Description 정보가 추가 되었습니다. 확인해 주세요."),
    JD_AGREE("email/email-template", "[LMS/Binder/%s] Job Description 동의 알림", "Job Description 동의 처리가 완료 되었습니다. 확인해 주세요."),
    JD_APPROVED("email/email-template", "[LMS/Binder/%s] Job Description 승인 알림", "Job Description이 승인 되었습니다.");

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
