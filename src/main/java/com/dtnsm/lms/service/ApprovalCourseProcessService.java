package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.controller.CourseAdminController;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.*;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.GlobalUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalCourseProcessService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAdminController.class);

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

    @Autowired
    CourseAccountOrderService courseAccountOrderService;

    @Autowired
    SignatureService signatureService;

//    public void courseRequestProcess(Account account, Course course) {
//        Account apprAccount = userService.getAccountByUserId(account.getParentUserId());
//        CourseAccountOrder courseAccountOrder2 = new CourseAccountOrder();
//        courseAccountOrder2.setFUser(userService.getAccountByUserId(account.getParentUserId()));
//        courseAccountOrder2.setFCourseAccount(null);
//        // fKind : 0:초기, 1:결재, 2. 합의, 3:확인
//        courseAccountOrder2.setFKind("1");
//        courseAccountOrder2.setFStatus("0");
//        courseAccountOrder2.setFSeq(1);
//        courseAccountOrder2.setFNext("1");
//
//        courseAccountOrderService.save(courseAccountOrder2);
//    }

    // 교육 신청 처리
    public void courseRequestProcess(Account account, Course course, String requestType, String fromDate, String toDate) {

        // 팀장/부서장 승인여부
        String isAppr1 = course.getCourseMaster().getIsTeamMangerApproval();
        // 관리자 승인여부
        String isAppr2 = course.getCourseMaster().getIsCourseMangerApproval();

        // 교육신청
        CourseAccount courseAccount = new CourseAccount();
        courseAccount.setCourse(course);
        courseAccount.setAccount(account);
        courseAccount.setRequestDate(DateUtil.getTodayString());
        courseAccount.setFWdate(DateUtil.getToday());
        courseAccount.setRequestType(requestType);        // 교육신청(0:관리자지정, 1:사용자 신청)
        courseAccount.setCourseStatus(CourseStepStatus.request);
        courseAccount.setFStatus("0");
        courseAccount.setFCurrSeq(1);

        // 결재자수 Max 설정
        if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
            courseAccount.setFFinalCount(2);
            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
        } else if(isAppr1.equals("Y")) {
            courseAccount.setFFinalCount(1);
            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
        } else {
            courseAccount.setFFinalCount(0);
            courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
            courseAccount.setFStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
            courseAccount.setCourseStatus(CourseStepStatus.process);
        }

        CourseAccount saveCourseAccount = courseAccountService.save(courseAccount);

        // 전자결재가 있는 경우
        if(saveCourseAccount.getIsApproval().equals("1")) {

            // 내결재사항을 추가한다.
            CourseAccountOrder courseAccountOrder = new CourseAccountOrder();
            courseAccountOrder.setFUser(account);
            courseAccountOrder.setSignature(signatureService.getSign(account.getUserId()));
            courseAccountOrder.setCourseAccount(saveCourseAccount);

            // fKind : 0:초기, 1:결재, 2. 합의, 3:확인
            courseAccountOrder.setFKind("1");
            courseAccountOrder.setFStatus("1");
            courseAccountOrder.setFSeq(0);
            courseAccountOrder.setFNext("0");
            courseAccountOrder.setFDate(DateUtil.getToday());
            courseAccountOrder.setFComment("");
            courseAccountOrderService.save(courseAccountOrder);

            //courseAccount.addCourseAccountOrder(courseAccountOrderService.save(courseAccountOrder1));

            Account apprAccount;
            if (isAppr1.equals("Y")) {
                Account apprAccount2 = userService.getAccountByUserId(account.getParentUserId());
                CourseAccountOrder courseAccountOrder2 = new CourseAccountOrder();
                courseAccountOrder2.setFUser(apprAccount2);
                courseAccountOrder2.setCourseAccount(saveCourseAccount);
                // fKind : 0:초기, 1:결재, 2. 합의, 3:확인
                courseAccountOrder2.setFKind("1");
                courseAccountOrder2.setFStatus("0");
                courseAccountOrder2.setFSeq(1);
                courseAccountOrder2.setFNext("1");

                courseAccountOrderService.save(courseAccountOrder2);

                //courseAccount.addCourseAccountOrder(courseAccountOrderService.save(courseAccountOrder1));

                // 1차 결재자 메일 전송
                sendMail(apprAccount2, course, MailSendType.REQUEST);
            }

            if (isAppr2.equals("Y")) {
                Account apprAccount3 = userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId());
                CourseAccountOrder courseAccountOrder3 = new CourseAccountOrder();
                courseAccountOrder3.setFUser(apprAccount3);
                courseAccountOrder3.setCourseAccount(saveCourseAccount);
                courseAccountOrder3.setFKind("1");
                courseAccountOrder3.setFStatus("0");
                courseAccountOrder3.setFSeq(2);

               courseAccountOrderService.save(courseAccountOrder3);

                //courseAccount.addCourseAccountOrder(courseAccountOrderService.save(courseAccountOrder1));
            }
        }

       // 과정 결재 정보 생성
        //CourseAccount courseAccount1 = courseAccountService.save(courseAccount);

        // 과정 생성
        // TODO : 교육신청시 교육과정 생성 유무

        // 사용자별 과정 생성
        createUserCourse(saveCourseAccount, course.getFromDate(), course.getToDate());
    }


    // 교육과정 생성
    public void createUserCourse(CourseAccount courseAccount, String fromDate, String toDate) {
        Account account = courseAccount.getAccount();
        Course course = courseAccount.getCourse();

        // 강의 생성
        if(course.getSections().size() > 0) {
            for (CourseSection courseSection : course.getSections()) {
                CourseSectionAction courseSectionAction = new CourseSectionAction();
                courseSectionAction.setCourseAccount(courseAccount);
                courseSectionAction.setAccount(account);
                courseSectionAction.setCourseSection(courseSection);
                courseSectionAction.setTotalUseSecond(0);
                courseSectionAction.setRunCount(0);
                courseSectionAction.setStatus(SectionStatusType.REQUEST);
                courseSectionAction.setFromDate(fromDate);  // 개인별 교육기간 설정
                courseSectionAction.setToDate(toDate);      // 개인별 교육기간 설정
                sectionActionService.save(courseSectionAction);
            }
        }

        // 시험 생성
        if(course.getIsQuiz().equals("Y") && course.getQuizzes().size() > 0) {
            for (CourseQuiz courseQuiz : course.getQuizzes()) {
                CourseQuizAction courseQuizAction = new CourseQuizAction();
                courseQuizAction.setCourseAccount(courseAccount);
                courseQuizAction.setAccount(account);
                courseQuizAction.setQuiz(courseQuiz);
                courseQuizAction.setTotalUseSecond(0);
                courseQuizAction.setRunCount(0);
                courseQuizAction.setStatus(QuizStatusType.REQUEST);
                courseQuizAction.setFromDate(fromDate);  // 개인별 교육기간 설정
                courseQuizAction.setToDate(toDate);      // 개인별 교육기간 설정
                courseQuizAction.setQuestionCount(courseQuiz.getQuizQuestions().size());
                quizActionService.saveQuizAction(courseQuizAction);
            }
        }

        // 설문 생성
        if(course.getIsSurvey().equals("Y") && course.getSurveys().size() > 0) {
            for (CourseSurvey courseSurvey : course.getSurveys()) {
                CourseSurveyAction courseSurveyAction = new CourseSurveyAction();
                courseSurveyAction.setCourseAccount(courseAccount);
                courseSurveyAction.setAccount(account);
                courseSurveyAction.setCourseSurvey(courseSurvey);
                courseSurveyAction.setStatus(SurveyStatusType.REQUEST);
                courseSurveyAction.setFromDate(fromDate);  // 개인별 교육기간 설정
                courseSurveyAction.setToDate(toDate);      // 개인별 교육기간 설정
                courseSurveyAction.setQuestionCount(courseSurvey.getQuestions().size());
                surveyActionService.saveSurveyAction(courseSurveyAction);
            }
        }

        // 알림 등록
    }


    // 교육 1차 승인 처리
    public void courseApproval1Proces(CourseAccountOrder courseAccountOrder) {

        int finalCount = courseAccountOrder.getCourseAccount().getFFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFUser().getUserId()));
        courseAccountOrder.setFDate(DateUtil.getToday());
        courseAccountOrder.setFNext("0");
        courseAccountOrder.setFStatus("1");
        courseAccountOrder.setFComment("");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        if(finalCount == courseAccountOrder.getFSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            courseAccount.setFStatus("1");
            courseAccount.setCourseStatus(CourseStepStatus.process);

            courseAccountService.save(courseAccount);

            // 최종 승인이면 기안자에게 메일 전송
            sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.APPROVAL1);
        } else {
            CourseAccountOrder nextOrder = courseAccountOrderService.getByFnoAndSeq(courseAccountOrder.getCourseAccount().getId(), courseAccountOrder.getFSeq()+1);
            if (nextOrder != null) {

                nextOrder.setFNext("1");
                nextOrder = courseAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                courseAccount.setFCurrSeq(nextOrder.getFSeq());
                courseAccountService.save(courseAccount);

                sendMail(nextOrder.getFUser(), courseAccount.getCourse(), MailSendType.APPROVAL1);
            }
        }
    }

    // 교육 1차 기각 처리
    public void courseReject1Proces(CourseAccountOrder courseAccountOrder) {

        int finalCount = courseAccountOrder.getCourseAccount().getFFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFUser().getUserId()));
        courseAccountOrder.setFDate(DateUtil.getToday());
        courseAccountOrder.setFNext("0");
        courseAccountOrder.setFStatus("2");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        courseAccount.setFStatus("2");
        courseAccount.setCourseStatus(CourseStepStatus.reject);
        courseAccount.setIsCommit("1");

        courseAccountService.save(courseAccount);

        // 기안자에게 메일 전송
        sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.REJECT);


//        if(finalCount == 1) {   // 종결처리
//            //  승인: 1, 기각 : 2
//
//        } else {
//
//            CourseAccountOrder nextOrder = courseAccountOrderService.getByFnoAndSeq(courseAccountOrder.getCourseAccount().getId(), courseAccountOrder.getFSeq()+1);
//            if (nextOrder != null) {
//                nextOrder.setFNext("1");
//                nextOrder = courseAccountOrderService.save(nextOrder);
//
//                sendMail(nextOrder.getFUser(), courseAccount.getCourse(), MailSendType.REJECT);
//            }
//        }
    }

    // 교육 2차 승인 처리
    public void courseApproval2Proces(CourseAccountOrder courseAccountOrder) {

        int finalCount = courseAccountOrder.getCourseAccount().getFFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFUser().getUserId()));
        courseAccountOrder.setFDate(DateUtil.getToday());
        courseAccountOrder.setFNext("0");
        courseAccountOrder.setFStatus("1");
        courseAccountOrder.setFComment("");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        if(finalCount == courseAccountOrder.getFSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            courseAccount.setFStatus("1");
            courseAccount.setCourseStatus(CourseStepStatus.process);

            courseAccountService.save(courseAccount);

            // 최종 승인이면 기안자에게 메일 전송
            sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.APPROVAL2);
        } else {

            CourseAccountOrder nextOrder = courseAccountOrderService.getByFnoAndSeq(courseAccountOrder.getCourseAccount().getId(), courseAccountOrder.getFSeq()+1);
            if (nextOrder != null) {
                nextOrder.setFNext("1");
                nextOrder = courseAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                courseAccount.setFCurrSeq(nextOrder.getFSeq());
                courseAccountService.save(courseAccount);

                sendMail(nextOrder.getFUser(), courseAccount.getCourse(), MailSendType.APPROVAL2);
            }
        }
    }

    // 교육 2차 기각 처리
    public void courseReject2Proces(CourseAccountOrder courseAccountOrder) {

        int finalCount = courseAccountOrder.getCourseAccount().getFFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFUser().getUserId()));
        courseAccountOrder.setFDate(DateUtil.getToday());
        courseAccountOrder.setFNext("0");
        courseAccountOrder.setFStatus("2");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        courseAccount.setFStatus("2");
        courseAccount.setCourseStatus(CourseStepStatus.reject);
        courseAccount.setIsCommit("1");

        courseAccountService.save(courseAccount);

        // 기안자에게 메일 전송
        sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.REJECT);

//        if(finalCount == 2) {   // 종결처리
//            //  승인: 1, 기각 : 2
//            courseAccount.setFStatus("2");
//
//            courseAccountService.save(courseAccount);
//
//            // 최종 승인이면 기안자에게 메일 전송
//            sendMail(courseAccount.getAccount(), courseAccount.getCourse(), MailSendType.REJECT);
//        } else {
//
//            CourseAccountOrder nextOrder = courseAccountOrderService.getByFnoAndSeq(courseAccountOrder.getCourseAccount().getId(), courseAccountOrder.getFSeq()+1);
//            if (nextOrder != null) {
//                nextOrder.setFNext("1");
//                nextOrder = courseAccountOrderService.save(nextOrder);
//
//                sendMail(nextOrder.getFUser(), courseAccount.getCourse(), MailSendType.REJECT);
//            }
//        }
    }

    // 교육과정 메일 전송
    public void sendMail(Account account, Course course, MailSendType mailSendType) {
        Mail mail = new Mail();
        mail.setEmail(account.getEmail());      // Email
        mail.setObject(course.getTitle());      // Subject
        mail.setMessage(course.getContent());   // Content
        mailService.send(mail, mailSendType);

    }
}
