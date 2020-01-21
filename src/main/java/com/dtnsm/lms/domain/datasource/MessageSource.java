package com.dtnsm.lms.domain.datasource;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.LmsAlarmCourseType;
import com.dtnsm.lms.domain.constant.LmsAlarmGubun;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageSource {
    // Page Title
    private String title;

    private LmsAlarmGubun alarmGubun;

    private LmsAlarmCourseType lmsAlarmCourseType;

    private CourseAccount courseAccount;


    // 제목
    private String subject;

    // 내용
    private String content;

    private Course course;

    private Document document;

    // 신청자
    private Account sender;

    // 수신자
    private Account receive;

//    public MessageSource(String title, String subject, String content, String sender, String receive) {
//        this.title = title;
//        this.subject = subject;
//        this.content = content;
//        this.sender = sender;
//        this.receive = receive;
//    }
//
//    public MessageSource(){}
}
