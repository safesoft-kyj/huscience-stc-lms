package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.constant.LmsAlarmCourseType;
import com.dtnsm.lms.domain.constant.MailSendType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

import static com.dtnsm.lms.domain.constant.LmsAlarmCourseType.Request;

@Component
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;


    public ResponseEntity<?> sendMail(Mail mail){
        send(mail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public void send(Mail mail) {
        send(mail, Request);
    }

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
        sendPreparedMail(mail.getEmail(), mail.getObject(), body, true);
    }

    private void sendPreparedMail(String to, String subject, String text, Boolean isHtml) {
        LOGGER.error("Problem with sending email to: {}, error message: {}", to, subject);

        new Thread(() -> {
            try {
                MimeMessage mail = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text, isHtml);
                javaMailSender.send(mail);
            } catch (Exception e) {
                LOGGER.error("Problem with sending email to: {}, error message: {}", to, e.getMessage());
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