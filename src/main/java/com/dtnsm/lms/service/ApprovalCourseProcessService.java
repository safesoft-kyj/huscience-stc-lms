package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.*;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApprovalCourseProcessService {

    @Autowired
    CourseAccountRepository courseAccountRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CourseManagerService courseManagerService;

    @Autowired
    private CourseSectionActionService sectionActionService;

    @Autowired
    private CourseSurveyActionService surveyActionService;

    @Autowired
    private CourseQuizActionService quizActionService;

    @Autowired
    private MailService mailService;

    @Autowired CourseAccountService courseAccountService;

    // 교육 신청 처리
    public void courseRequestProcess(Account account, Course course) {

        // 교육신청
        CourseAccount courseAccount = new CourseAccount();
        courseAccount.setCourse(course);
        courseAccount.setAccount(account);
        courseAccount.setRequestDate(DateUtil.getTodayString());
        courseAccount.setRequestType(CourseRequestType.SPECIFY);        // 교육신청유형(관리자지정, 사용자 신청)

        courseAccount.setApprUserId1(userService.getAccountByUserId(account.getParentUserId()));
        courseAccount.setApprUserId2(userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId()));
        courseAccount.setIsTeamMangerApproval(course.getCourseMaster().getIsTeamMangerApproval());
        courseAccount.setIsCourseMangerApproval(course.getCourseMaster().getIsCourseMangerApproval());

        // 기본상태 지정
        courseAccount.setApprovalStatus(ApprovalStatusType.REQUEST_DONE);   // 결재상태
        courseAccount.setIsCommit("0"); // 결재 승인 프로세스 완료 상태
        courseAccount.setIsAppr1("N");  // 1차 결재 완료 여부
        courseAccount.setIsAppr2("N");  // 2차 결재 완료 여부

        // 1차 팀장 결재 유무
        if (course.getCourseMaster().getIsTeamMangerApproval().equals("N") && course.getCourseMaster().getIsCourseMangerApproval().equals("N")) {
            courseAccount.setApprovalStatus(ApprovalStatusType.APPROVAL_MANAGER_DONE);    // 최종승인완료
            courseAccount.setIsAppr1("Y");
            courseAccount.setIsAppr2("Y");
            courseAccount.setIsCommit("1");
        }

        // 과정 결재 정보 생성
        CourseAccount courseAccount1 = courseAccountService.save(courseAccount);

        course = courseAccount1.getCourse();

        // 강의 생성
        if(course.getSections().size() > 0) {
            for (CourseSection courseSection : course.getSections()) {
                CourseSectionAction courseSectionAction = new CourseSectionAction();
                courseSectionAction.setAccount(account);
                courseSectionAction.setCourseSection(courseSection);
                courseSectionAction.setTotalUseSecond(0);
                courseSectionAction.setRunCount(0);
                courseSectionAction.setStatus(SectionStatusType.REQUEST);
                sectionActionService.save(courseSectionAction);
            }
        }

        // 시험 생성
        if(course.getIsQuiz().equals("Y") && course.getQuizzes().size() > 0) {
            for (CourseQuiz courseQuiz : course.getQuizzes()) {
                CourseQuizAction courseQuizAction = new CourseQuizAction();
                courseQuizAction.setAccount(account);
                courseQuizAction.setQuiz(courseQuiz);
                courseQuizAction.setTotalUseSecond(0);
                courseQuizAction.setRunCount(0);
                courseQuizAction.setStatus(QuizStatusType.REQUEST);
                quizActionService.saveQuizAction(courseQuizAction);
            }
        }

        // 설문 생성
        if(course.getIsSurvey().equals("Y") && course.getSurveys().size() > 0) {
            for (CourseSurvey courseSurvey : course.getSurveys()) {
                CourseSurveyAction courseSurveyAction = new CourseSurveyAction();
                courseSurveyAction.setAccount(account);
                courseSurveyAction.setCourseSurvey(courseSurvey);
                courseSurveyAction.setStatus(SurveyStatusType.REQUEST);
                surveyActionService.saveSurveyAction(courseSurveyAction);
            }
        }

        // 1차 결재자 메일 전송
        if(courseAccount1.getIsTeamMangerApproval().equals("Y") && courseAccount1.getApprUserId1() != null) {
            sendMail(courseAccount1.getApprUserId1(), courseAccount1.getCourse(), MailSendType.REQUEST);
        }

        // 2차 결재자 메일 전송
        if(courseAccount1.getIsCourseMangerApproval().equals("Y") && courseAccount1.getApprUserId2() != null) {
            sendMail(courseAccount1.getApprUserId2(), courseAccount1.getCourse(), MailSendType.REQUEST);
        }

        // 알림 등록
    }

    // 교육 1차 승인 처리
    public void courseApproval1Proces(CourseAccount courseAccount) {

        // 팀장 결재 승인 설정이 Y이고 아직승인되지 않고 완료되지 않은 건
        if(courseAccount.getIsTeamMangerApproval().equals("Y") && courseAccount.getIsAppr1().equals("N") && courseAccount.getIsCommit().equals("0")) {

            courseAccount.setIsAppr1("Y");
            courseAccount.setApprStatus1(ApprovalStatus.approval);
            courseAccount.setApprDate1(DateUtil.getTodayString());
            courseAccount.setApprDateTime1(new Date());

            // 최종승인이 팀장인 경우 상태를 종결한다.
            if (courseAccount.getIsTeamMangerApproval().equals("Y") && courseAccount.getIsCourseMangerApproval().equals("N")) {
                courseAccount.setApprovalStatus(ApprovalStatusType.APPROVAL_COMPLETE);

                // 최종 승인이면 기안자에게 메일 전송
                if(courseAccount.getIsCommit().equals("Y") && courseAccount.getAccount() != null) {
                    sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.REQUEST);
                }
            } else {

                courseAccount.setApprovalStatus(ApprovalStatusType.APPROVAL_TEAM_DONE);
                // 2차 결재가 있으면 2차 결재자 메일 전송
                if(courseAccount.getIsCourseMangerApproval().equals("Y") && courseAccount.getApprUserId2() != null) {
                    sendMail(courseAccount.getApprUserId2(), courseAccount.getCourse(), MailSendType.REQUEST);
                }
            }

            courseAccount = courseAccountService.save(courseAccount);
        }
    }

    // 교육 1차 기각 처리
    public void courseReject1Proces(CourseAccount courseAccount) {

        if(courseAccount.getIsTeamMangerApproval().equals("Y") && courseAccount.getIsAppr1().equals("N") && courseAccount.getIsCommit().equals("0")) {

            courseAccount.setIsAppr1("Y");
            courseAccount.setApprStatus1(ApprovalStatus.reject);
            courseAccount.setApprDate1(DateUtil.getTodayString());
            courseAccount.setApprDateTime1(new Date());
            courseAccount.setApprovalStatus(ApprovalStatusType.APPROVAL_TEAM_REJECT);
            courseAccount.setIsCommit("1"); // 2차 승인이 진행되지 않게 완료 처리한다.

            courseAccountService.save(courseAccount);
        }

        // 2차 결재가 있으면 2차 결재자 메일 전송
        if(courseAccount.getIsCourseMangerApproval().equals("Y") && courseAccount.getApprUserId2() != null) {
            sendMail(courseAccount.getApprUserId2(), courseAccount.getCourse(), MailSendType.REQUEST);
        }

        // 최종 승인이면 기안자에게 메일 전송
        if(courseAccount.getIsCommit().equals("Y") && courseAccount.getAccount() != null) {
            sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.REQUEST);
        }
    }

    // 교육 2차 승인 처리
    public void courseApproval2Proces(CourseAccount courseAccount) {

        // 2차승인여부가 Y이고 아직 미승인된 경우만 처리
        if(courseAccount.getIsCourseMangerApproval().equals("Y") && courseAccount.getIsAppr2().equals("N")) {
            courseAccount.setIsAppr2("Y");
            courseAccount.setApprStatus2(ApprovalStatus.approval);
            courseAccount.setApprDate2(DateUtil.getTodayString());
            courseAccount.setApprDateTime2(new Date());
            courseAccount.setApprovalStatus(ApprovalStatusType.APPROVAL_TEAM_DONE);
            courseAccount.setIsCommit("1");

            courseAccountService.save(courseAccount);
        }

        // 최종 승인이면 기안자에게 메일 전송
        if(courseAccount.getIsCommit().equals("Y") && courseAccount.getAccount() != null) {
            sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.REQUEST);
        }
    }

    // 교육 2차 기각 처리
    public void courseReject2Proces(CourseAccount courseAccount) {

        // 2차승인여부가 Y이고 아직 미승인된 경우만 처리
        if(courseAccount.getIsCourseMangerApproval().equals("Y") && courseAccount.getIsAppr2().equals("N")) {
            courseAccount.setIsAppr2("Y");
            courseAccount.setApprStatus2(ApprovalStatus.reject);
            courseAccount.setApprDate2(DateUtil.getTodayString());
            courseAccount.setApprDateTime2(new Date());
            courseAccount.setApprovalStatus(ApprovalStatusType.APPROVAL_MANAGER_REJECT);
            courseAccount.setIsCommit("1");

            courseAccountService.save(courseAccount);
        }

        // 최종 승인이면 기안자에게 메일 전송
        if(courseAccount.getIsCommit().equals("Y") && courseAccount.getAccount() != null) {
            sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.REQUEST);
        }
    }

    // 교육과정 메일 전송
    public void sendMail(Account account, Course course, MailSendType mailSendType) {
        Mail mail = new Mail();
        mail.setEmail(account.getEmail());      // Email
        mail.setObject(course.getTitle());      // Subject
        mail.setMessage(course.getContent());   // Content
        mailService.send(mail, MailSendType.REQUEST);

    }
}
