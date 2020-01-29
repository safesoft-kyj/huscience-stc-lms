package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.constant.BinderAlarmType;
import com.dtnsm.lms.domain.constant.LmsAlarmCourseType;
import com.dtnsm.lms.domain.datasource.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

import static com.dtnsm.lms.domain.constant.LmsAlarmCourseType.Request;

@Component
public class MailService {

//    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;


    public ResponseEntity<?> sendMail(Mail mail){
        send(mail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 교육과정 공통 메일
    public void send(MessageSource messageSource) {
        String templateUri = "email/email-common";
        final Context context = new Context();
        context.setVariable("messageSource", messageSource);

        String body = templateEngine.process(templateUri, context);

        //send the html template
        sendPreparedMail(messageSource.getReceive().getEmail(), "[LMS] " + messageSource.getSubject(), body, true);
    }

    public void send(Mail mail) {
        send(mail, Request);
    }

    // 사용자 생성시 발송 메일
    public void sendAccount(Mail mail) {
        //get and fill the template
        final Context context = new Context();
        context.setVariable("subject", mail.getObject());
        context.setVariable("message", mail.getMessage());
        String templateUri = "email/email-account";

        String body = templateEngine.process(templateUri, context);

        //send the html template
        sendPreparedMail(mail.getEmail(), mail.getObject(), body, true);
    }

    // 게시판 데이터 추가시 발송 메일(게시판 기준정보에서 메일 발송 Y를 선택한 경우)
    public void sendBorder(Mail mail) {
        //get and fill the template
        final Context context = new Context();
        context.setVariable("subject", mail.getObject());
        context.setVariable("message", mail.getMessage());
        String templateUri = "email/email-border";

        String body = templateEngine.process(templateUri, context);

        //send the html template
        sendPreparedMail(mail.getEmail(), mail.getObject(), body, true);
    }

    // 교육과정 email
    public void send(Mail mail, LmsAlarmCourseType lmsAlarmCourseType) {
        //get and fill the template
        final Context context = new Context();
        context.setVariable("subject", mail.getObject());
        context.setVariable("message", mail.getMessage());
        String templateUri = "email/email-template";

        switch (lmsAlarmCourseType) {
            case CourseAccountAssign:
                templateUri = "email/course-account-assign";
                break;
            case CourseToDateApproach:
                templateUri = "email/course-todate-approach";
                break;
            case CourseReportApproach:
                templateUri = "email/course-report-approach";
                break;
            case Request:
                templateUri = "email/course-request";
                break;
            case Approval:
                templateUri = "email/course-appr1";
                break;
            case Reject:
                templateUri = "email/course-reject";
                // 메일 전송
                break;
        }

        String body = templateEngine.process(templateUri, context);

        //send the html template
        sendPreparedMail(mail.getEmail(), "[LMS] " + mail.getObject(), body, true);
    }

    public void send(Mail mail, LmsAlarmCourseType lmsAlarmCourseType, String message) {
        //get and fill the template
        final Context context = new Context();
        context.setVariable("subject", mail.getObject());
        context.setVariable("message", mail.getMessage());
        String templateUri = "email/email-template";

        switch (lmsAlarmCourseType) {
            case CourseAccountAssign:
                templateUri = "email/course-account-assign";
                break;
            case CourseToDateApproach:
                templateUri = "email/course-todate-approach";
                break;
            case CourseReportApproach:
                templateUri = "email/course-report-approach";
                break;
            case Request:
                templateUri = "email/course-request";
                break;
            case Approval:
                templateUri = "email/course-appr1";
                break;
            case Reject:
                templateUri = "email/course-reject";
                // 메일 전송
                break;
        }

        String body = templateEngine.process(templateUri, context);

        //send the html template
        sendPreparedMail(mail.getEmail(), "[LMS] " + mail.getObject(), body, true);
    }

    /**
     * Binder 관련 알람 처리
     */
    public void send(String to, String subject, BinderAlarmType binderAlarmType, Context context) {
        String body = templateEngine.process(binderAlarmType.getTemplate(), context);
        sendPreparedMail(to, subject, body, true);
    }

    private void sendPreparedMail(String to, String subject, String text, Boolean isHtml) {
//        LOGGER.error("Problem with sending email to: {}, error message: {}", to, subject);

        new Thread(() -> {
            try {
                MimeMessage mail = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text, isHtml);
                javaMailSender.send(mail);
            } catch (Exception e) {
//                LOGGER.error("Problem with sending email to: {}, error message: {}", to, e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

//    private void sendPreparedMail(String to, String subject, String text, Boolean isHtml) {
//        try {
//            MimeMessage mail = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mail, false);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text, isHtml);
//            javaMailSender.send(mail);
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            LOGGER.error("Problem with sending email to: {}, error message: {}", to, e.getMessage());
//        }
//    }

}