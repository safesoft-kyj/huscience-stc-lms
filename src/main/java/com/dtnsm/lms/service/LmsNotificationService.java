package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.LmsAlarmCourseType;
import com.dtnsm.lms.domain.constant.LmsAlarmGubun;
import com.dtnsm.lms.domain.constant.LmsAlarmType;
import com.dtnsm.lms.domain.datasource.MessageSource;
import com.dtnsm.lms.repository.LmsNotificationRepository;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class LmsNotificationService {

    @Autowired
    LmsNotificationRepository lmsNotificationRepository;

    @Autowired
    UserService userService;

    public List<LmsNotification> getAllByUserNotification(String userId) {
        return lmsNotificationRepository.findAllByAccount_UserId(userId);
    }

    public List<LmsNotification> getAllByCourseIdAndUserNotification(Long courseId, String userId) {
        return lmsNotificationRepository.findAllByCourse_IdAndAccount_UserId(courseId, userId);
    }

    public List<LmsNotification> getAllByCourseIdNotification(Long courseId) {
        return lmsNotificationRepository.findAllByCourse_Id(courseId);
    }

    public List<LmsNotification> getAllByDocumentIdNotification(Long docId) {
        return lmsNotificationRepository.findAllByDocument_Id(docId);
    }

    public List<LmsNotification> getTop5ByUserNotification(String userId) {
        return lmsNotificationRepository.findTop5ByAccount_UserIdOrderByCreatedDateDesc(userId);
    }

    public LmsNotification save(LmsNotification lmsNotification) {

        return lmsNotificationRepository.save(lmsNotification);
    }

    public void delete(LmsNotification lmsNotification) {
        lmsNotificationRepository.delete(lmsNotification);
    }


    // 시스템 기본 정보에 대한 알람 메세지
    public void sendAlarm(LmsAlarmType lmsAlarmType, Account account) {

        LmsNotification lmsNotification = new LmsNotification();

        if (lmsAlarmType == LmsAlarmType.ParentUser) {
            lmsNotification.setAlarmGubun(LmsAlarmGubun.WARNING);
            lmsNotification.setTitle("<a class='text-info' href='http://lms.dtnsm.com/mypage/myInfo'>상위결재권자 미설정</a>");
            lmsNotification.setContent("상위결재권자는 교육신청이나 전자결재 전 필히 지정하셔야 합니다.");

        } else if (lmsAlarmType == LmsAlarmType.Sign) {
            lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
            lmsNotification.setTitle("<a class='text-info' href='http://lms.dtnsm.com/mypage/myInfo'>사인 미등록<a>");
            lmsNotification.setContent("사인이 미등록 되었습니다.");

        } else if (lmsAlarmType == LmsAlarmType.Manager) {
            lmsNotification.setAlarmGubun(LmsAlarmGubun.WARNING);
            lmsNotification.setTitle("과정 관리자 미등록");
            lmsNotification.setContent("과정 관리자는 필히 지정하셔야 합니다.");
        }

        lmsNotification.setAccount(account);
        // C:과정, D:전자결재
        lmsNotification.setGubun("C");
        lmsNotificationRepository.save(lmsNotification);
    }

    public void sendMessage(MessageSource messageSource) {

        LmsNotification lmsNotification = new LmsNotification();
        lmsNotification.setAlarmGubun(messageSource.getAlarmGubun());
        lmsNotification.setTitle(messageSource.getSubject());
        lmsNotification.setContent(messageSource.getContent());
        lmsNotification.setAccount(messageSource.getReceive());

        if (messageSource.getCourse() != null) {
            lmsNotification.setCourse(messageSource.getCourse());
            lmsNotification.setGubun("C");
        }
        if (messageSource.getDocument() != null) {
            lmsNotification.setDocument(messageSource.getDocument());
            lmsNotification.setGubun("D");
        }

        lmsNotificationRepository.save(lmsNotification);
    }

    // 교육 프로세스 관련 메세지
    public void sendMessage(LmsAlarmCourseType lmsAlarmCourseType, Account account, Course course, String message) {

        LmsNotification lmsNotification = new LmsNotification();

        switch (lmsAlarmCourseType) {
            case CourseAccountAssign:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(course.getTitle() +  "과정 교육 대상자로 지정되셨습니다.");
                lmsNotification.setContent(message);
                break;
            case CourseToDateApproach:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(message);
                lmsNotification.setContent(message);
                break;
            case CourseReportApproach:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(course.getTitle() +  "외부교육 참석보고서를 3일이내(" + DateUtil.getStringDateAddDay(course.getToDate(), 3) + ") 작성해 주세요.");
                lmsNotification.setContent(message);
            case Request:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(course.getTitle() +  "과정 교육결재 요청 되었습니다..");
                lmsNotification.setContent(message);
                break;

            case Approval:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(course.getTitle() +  "과정 승인 처리 되었습니다.");
                lmsNotification.setContent(message);
                break;
            case Reject:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(course.getTitle() +  "과정 반려 처리 되었습니다.");
                lmsNotification.setContent(message);
                break;
        }

        lmsNotification.setAccount(account);
        lmsNotification.setCourse(course);
        lmsNotification.setGubun("C");
        lmsNotificationRepository.save(lmsNotification);
    }






    // 교육 프로세스 관련 메세지
    public void sendMessage(LmsAlarmCourseType lmsAlarmCourseType, Account account, Document document, String message) {

        LmsNotification lmsNotification = new LmsNotification();

        switch (lmsAlarmCourseType) {
            case CourseAccountAssign:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(document.getTitle() +  " 교육 대상자로 지정되셨습니다.");
                lmsNotification.setContent(message);
                break;
            case Request:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(document.getTitle() +  " 교육 신청 처리 되었습니다..");
                lmsNotification.setContent(message);
                break;

            case Approval:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(document.getTitle() +  " 승인 처리 되었습니다.");
                lmsNotification.setContent(message);
                break;
            case Reject:
                lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
                lmsNotification.setTitle(document.getTitle() +  " 반려 처리 되었습니다.");
                lmsNotification.setContent(message);
                break;
        }

        lmsNotification.setAccount(account);
        lmsNotification.setDocument(document);
        lmsNotification.setGubun("D");
        lmsNotificationRepository.save(lmsNotification);
    }
}
