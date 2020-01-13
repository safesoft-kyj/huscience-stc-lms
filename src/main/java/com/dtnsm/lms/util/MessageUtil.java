package com.dtnsm.lms.util;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.Document;
import com.dtnsm.lms.domain.constant.LmsAlarmCourseType;
import com.dtnsm.lms.domain.datasource.MessageSource;
import com.dtnsm.lms.service.LmsNotificationService;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

    public static LmsNotificationService lmsNotificationService;

    private static MailService mailService;

    @Autowired
    private MessageUtil(LmsNotificationService lmsNotificationService, MailService mailService){
        this.lmsNotificationService = lmsNotificationService;
        this.mailService = mailService;
    }

/*

    교육과정 관련 메세지

 */


    // 메세지 전송(알람 및 메일)
    public static void sendNotificationMessage(LmsAlarmCourseType lmsAlarmCourseType, Account account, Course course) {

        // 알람 발생
        sendNotificationMessage(lmsAlarmCourseType, account, course, "");
    }

    public static void sendNotificationMessage(LmsAlarmCourseType lmsAlarmCourseType, Account account, Course course, String message) {

        // 알람 발생
        sendNotification(lmsAlarmCourseType, account, course, message);

        // 메일 전송
        sendMail(lmsAlarmCourseType, account, course, message);
    }

    public static void sendNotificationMessage(MessageSource messageSource, boolean isMailSender) {

        // 알람 Message 저장
        lmsNotificationService.sendMessage(messageSource);

        // 메일 발송
        if(isMailSender) {
            mailService.send(messageSource);
        }
    }

    // 알람서비스
    public static void sendNotification(LmsAlarmCourseType lmsAlarmCourseType, Account account, Course course) {
        sendNotification(lmsAlarmCourseType, account, course, "");
    }

    public static void sendNotification(LmsAlarmCourseType lmsAlarmCourseType, Account account, Course course, String message) {
        lmsNotificationService.sendMessage(lmsAlarmCourseType, account, course, message);
    }

    // 교육과정 메일 전송
    public static void sendMail(LmsAlarmCourseType lmsAlarmCourseType, Account account, Course course) {
        Mail mail = new Mail();
        mail.setEmail(account.getEmail());      // Email
        mail.setObject(course.getTitle());      // Subject
        mail.setMessage(course.getContent());   // Content
        mailService.send(mail, lmsAlarmCourseType);
    }

    // 교육과정 메일 전송
    public static void sendMail(LmsAlarmCourseType lmsAlarmCourseType, Account account, Course course, String message) {
        Mail mail = new Mail();
        mail.setEmail(account.getEmail());      // Email
        mail.setObject(course.getTitle());      // Subject
        mail.setMessage(course.getContent());   // Content
        mailService.send(mail, lmsAlarmCourseType, message);
    }

    public static void sendMail(String email, MessageSource message) {
        Mail mail = new Mail();
        mail.setEmail(email);      // Email
        mail.setObject(message.getSubject());      // Subject
        mail.setMessage(message.getContent());   // Content
        mailService.send(mail);
    }


/*

    전자결재 관련 메세지

 */

    // 메세지 전송(알람 및 메일)
    public static void sendNotificationMessage(LmsAlarmCourseType lmsAlarmCourseType, Account account, Document document) {

        // 알람 발생
        sendNotificationMessage(lmsAlarmCourseType, account, document, "");
    }

    public static void sendNotificationMessage(LmsAlarmCourseType lmsAlarmCourseType, Account account, Document document, String message) {

        // 알람 발생
        sendNotification(lmsAlarmCourseType, account, document, message);

        // 메일 전송
        sendMail(lmsAlarmCourseType, account, document);
    }

    // 알람서비스
    public static void sendNotification(LmsAlarmCourseType lmsAlarmCourseType, Account account, Document document) {
        sendNotification(lmsAlarmCourseType, account, document, "");
    }

    public static void sendNotification(LmsAlarmCourseType lmsAlarmCourseType, Account account, Document document, String message) {
        lmsNotificationService.sendMessage(lmsAlarmCourseType, account, document, message);
    }

    public static void sendMail(LmsAlarmCourseType lmsAlarmCourseType, Account account, Document document) {
        Mail mail = new Mail();
        mail.setEmail(account.getEmail());      // Email
        mail.setObject(document.getTitle());      // Subject
        mail.setMessage(document.getContent());   // Content
        mailService.send(mail, lmsAlarmCourseType);
    }
}
