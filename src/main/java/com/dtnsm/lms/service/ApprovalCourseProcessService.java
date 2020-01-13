package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.controller.CourseAdminController;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.*;
import com.dtnsm.lms.domain.datasource.MessageSource;
import com.dtnsm.lms.repository.CourseAccountRepository;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.MessageUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalCourseProcessService {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseAdminController.class);

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
    CourseSectionActionService courseSectionActionService;

    @Autowired
    SignatureService signatureService;

    @Autowired
    CourseCertificateService courseCertificateService;

    @Autowired
    private LmsNotificationService lmsNotificationService;

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


//
//    // 1. Self 교육 신청
//    public CourseAccount courseAccountSelfProcess(Account account, Course course, String requestType) {
//
//        String fromDate;
//        String toDate;
//
//        // 팀장/부서장 승인여부
//        String isAppr1 = course.getCourseMaster().getIsTeamMangerApproval();
//        // 관리자 승인여부
//        String isAppr2 = course.getCourseMaster().getIsCourseMangerApproval();
//
//        // 교육신청
//        CourseAccount courseAccount = new CourseAccount();
//        courseAccount.setCourse(course);
//        courseAccount.setAccount(account);
//        courseAccount.setRequestDate(DateUtil.getTodayString());
//        courseAccount.setFWdate(DateUtil.getToday());
//        courseAccount.setRequestType(requestType);        // 교육신청(0:관리자지정, 1:사용자 신청)
//        courseAccount.setFStatus("0");
//        courseAccount.setFCurrSeq(1);
//
//        /*
//        교육과정에 따른 교육 기간 설정
//            1. 상시 교육
//                - 교육시작일자 : 신청일자
//                - 교육종료일자 : 과정의 교육일수로 계산
//            2. 상시교육이 아닌 경우
//                - 교육시작일자 : 개설과정의 교육시작일
//                - 교육종료일자 : 개설과정의 교육종료일
//        */
//        if (course.getIsAlways().equals("1")) {     // 상시교육일 경우
//            fromDate = DateUtil.getTodayString();
//            toDate = DateUtil.getStringDateAddDay(fromDate, course.getDay());
//        } else {    // 상시교육이 아닌 경우
//            fromDate = course.getFromDate();
//            toDate = course.getToDate();
//        }
//        courseAccount.setFromDate(fromDate);
//        courseAccount.setToDate(toDate);
//
//        // self 교육은 전자결재가 없음
//        courseAccount.setFFinalCount(0);
//        courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
//        courseAccount.setFStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
//        courseAccount.setIsAttendance("1"); // 교육참석유무(0:미참석, 1:참석) => 기본값 0
//
//        // 교육 대상자 지정인 경우는 상태를 신청 대기상태로 만든다
//        if (requestType.equals("0"))
//            courseAccount.setCourseStatus(CourseStepStatus.none);
//        else {  // 교육 대상자 신청인 경우는 상태를 신청상태로 변경한다.
//            courseAccount.setCourseStatus(CourseStepStatus.request);
//        }
//
//        CourseAccount saveCourseAccount = courseAccountService.save(courseAccount);
//
//        // 과정 생성
//        // TODO : 교육신청시 교육과정 생성 유무
//
//        // 강의 생성
//        if(course.getSections().size() > 0) {
//            for (CourseSection courseSection : course.getSections()) {
//                CourseSectionAction courseSectionAction = new CourseSectionAction();
//                courseSectionAction.setCourseAccount(saveCourseAccount);
//                courseSectionAction.setAccount(account);
//                courseSectionAction.setCourseSection(courseSection);
//                courseSectionAction.setTotalUseSecond(0);
//                courseSectionAction.setRunCount(0);
//                courseSectionAction.setStatus(SectionStatusType.REQUEST);
//                CourseSectionAction courseSectionAction1 = sectionActionService.save(courseSectionAction);
//            }
//        }
//
//        // 시험 생성
//        if(course.getIsQuiz().equals("Y") && course.getQuizzes().size() > 0) {
//            for (CourseQuiz courseQuiz : course.getQuizzes()) {
//                CourseQuizAction courseQuizAction = new CourseQuizAction();
//                courseQuizAction.setCourseAccount(saveCourseAccount);
//                courseQuizAction.setAccount(account);
//                courseQuizAction.setQuiz(courseQuiz);
//                courseQuizAction.setTotalUseSecond(0);
//                courseQuizAction.setRunCount(0);
//                courseQuizAction.setQuestionCount(courseQuiz.getQuizQuestions().size());
//                courseQuizAction.setStatus(QuizStatusType.REQUEST);
//                quizActionService.saveQuizAction(courseQuizAction);
//            }
//        }
//
//        // 설문 생성
//        if(course.getIsSurvey().equals("Y") && course.getSurveys().size() > 0) {
//            for (CourseSurvey courseSurvey : course.getSurveys()) {
//                CourseSurveyAction courseSurveyAction = new CourseSurveyAction();
//                courseSurveyAction.setCourseAccount(saveCourseAccount);
//                courseSurveyAction.setAccount(account);
//                courseSurveyAction.setCourseSurvey(courseSurvey);
//                courseSurveyAction.setQuestionCount(courseSurvey.getQuestions().size());
//                courseSurveyAction.setStatus(SurveyStatusType.REQUEST);
//                surveyActionService.saveSurveyAction(courseSurveyAction);
//            }
//        }
//
//
//        return saveCourseAccount;
//    }
//
//    // 2. Class 교육 신청
//    public CourseAccount courseAccountClassProcess(Account account, Course course, String requestType) {
//
//        String fromDate;
//        String toDate;
//
//        // 팀장/부서장 승인여부
//        String isAppr1 = course.getCourseMaster().getIsTeamMangerApproval();
//        // 관리자 승인여부
//        String isAppr2 = course.getCourseMaster().getIsCourseMangerApproval();
//
//        // 교육신청
//        CourseAccount courseAccount = new CourseAccount();
//        courseAccount.setCourse(course);
//        courseAccount.setAccount(account);
//        courseAccount.setRequestDate(DateUtil.getTodayString());
//        courseAccount.setFWdate(DateUtil.getToday());
//        courseAccount.setRequestType(requestType);        // 교육신청(0:관리자지정, 1:사용자 신청)
//        courseAccount.setFStatus("0");
//        courseAccount.setFCurrSeq(1);
//
//
//        /*
//        교육과정에 따른 교육 기간 설정
//            1. 상시 교육
//                - 교육시작일자 : 신청일자
//                - 교육종료일자 : 과정의 교육일수로 계산
//            2. 상시교육이 아닌 경우
//                - 교육시작일자 : 개설과정의 교육시작일
//                - 교육종료일자 : 개설과정의 교육종료일
//        */
//        if (course.getIsAlways().equals("1")) {     // 상시교육일 경우
//            fromDate = DateUtil.getTodayString();
//            toDate = DateUtil.getStringDateAddDay(fromDate, course.getDay());
//        } else {    // 상시교육이 아닌 경우
//            fromDate = course.getFromDate();
//            toDate = course.getToDate();
//        }
//        courseAccount.setFromDate(fromDate);
//        courseAccount.setToDate(toDate);
//
//        // 결재자수 Max 설정
//        if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
//            courseAccount.setFFinalCount(2);
//            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//        } else if(isAppr1.equals("Y")) {
//            courseAccount.setFFinalCount(1);
//            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//        } else {
//            courseAccount.setFFinalCount(0);
//            courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
//            courseAccount.setFStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
//        }
//
//        // 교육 대상자 지정인 경우는 상태를 신청 대기상태로 만든다
//        if (requestType.equals("0"))
//            courseAccount.setCourseStatus(CourseStepStatus.none);
//        else {  // 교육 대상자 신청인 경우는 상태를 신청상태로 변경한다.
//            courseAccount.setCourseStatus(CourseStepStatus.request);
//        }
//
//        // 과정 신청자 생성
//        CourseAccount saveCourseAccount = courseAccountService.save(courseAccount);
//
//        // 과정 생성
//        // TODO : 교육신청시 교육과정 생성 유무
//
//        // 강의 생성
//        if(course.getSections().size() > 0) {
//            for (CourseSection courseSection : course.getSections()) {
//                CourseSectionAction courseSectionAction = new CourseSectionAction();
//                courseSectionAction.setCourseAccount(saveCourseAccount);
//                courseSectionAction.setAccount(account);
//                courseSectionAction.setCourseSection(courseSection);
//                courseSectionAction.setTotalUseSecond(0);
//                courseSectionAction.setRunCount(0);
//                courseSectionAction.setStatus(SectionStatusType.REQUEST);
//                CourseSectionAction courseSectionAction1 = sectionActionService.save(courseSectionAction);
//
//                // class 교육은 offline 교육이므로 실제 강의를 종료시킨다.
//                // sectionActionService.UpdateCourseSectionActionComplete(courseSectionAction1);
//            }
//        }
//
//        // 시험 생성
//        if(course.getIsQuiz().equals("Y") && course.getQuizzes().size() > 0) {
//            for (CourseQuiz courseQuiz : course.getQuizzes()) {
//                CourseQuizAction courseQuizAction = new CourseQuizAction();
//                courseQuizAction.setCourseAccount(saveCourseAccount);
//                courseQuizAction.setAccount(account);
//                courseQuizAction.setQuiz(courseQuiz);
//                courseQuizAction.setTotalUseSecond(0);
//                courseQuizAction.setRunCount(0);
//                courseQuizAction.setQuestionCount(courseQuiz.getQuizQuestions().size());
//                courseQuizAction.setStatus(QuizStatusType.REQUEST);
//
//                // self외에는 실제 강의가 없기 때문에 상태를 ongoing 상태로 변경한다.
////                if (!courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {
////                    courseQuizAction.setStatus(QuizStatusType.ONGOING);
////                }
//
//                quizActionService.saveQuizAction(courseQuizAction);
//            }
//        }
//
//        // 설문 생성
//        if(course.getIsSurvey().equals("Y") && course.getSurveys().size() > 0) {
//            for (CourseSurvey courseSurvey : course.getSurveys()) {
//                CourseSurveyAction courseSurveyAction = new CourseSurveyAction();
//                courseSurveyAction.setCourseAccount(saveCourseAccount);
//                courseSurveyAction.setAccount(account);
//                courseSurveyAction.setCourseSurvey(courseSurvey);
//                courseSurveyAction.setQuestionCount(courseSurvey.getQuestions().size());
//                courseSurveyAction.setStatus(SurveyStatusType.REQUEST);
//
//                // self외에는 시험이 없는 경우 onGoing 상태로 변경한다
////                if (!courseAccount.getCourse().getCourseMaster().getId().equals("BC0101") && courseAccount.getCourse().getIsQuiz().equals("N")) {
////                    courseSurveyAction.setStatus(SurveyStatusType.ONGOING);
////                }
//
//                surveyActionService.saveSurveyAction(courseSurveyAction);
//            }
//        }
//
//        return saveCourseAccount;
//    }
//
//
//    // 3. 외부교육 신청
//    public CourseAccount courseAccountOtherProcess(CourseAccount courseAccount) {
//
//        String fromDate;
//        String toDate;
//
//        // 팀장/부서장 승인여부
//        String isAppr1 = courseAccount.getCourse().getCourseMaster().getIsTeamMangerApproval();
//        // 관리자 승인여부
//        String isAppr2 = courseAccount.getCourse().getCourseMaster().getIsCourseMangerApproval();
//
//        // 교육신청
//        CourseAccount courseAccount = new CourseAccount();
//        courseAccount.setRequestDate(DateUtil.getTodayString());
//        courseAccount.setFWdate(DateUtil.getToday());
//        courseAccount.setRequestType(requestType);        // 교육신청(0:관리자지정, 1:사용자 신청)
//        courseAccount.setFStatus("0");
//        courseAccount.setFCurrSeq(1);
//
//
//        /*
//        교육과정에 따른 교육 기간 설정
//            1. 상시 교육
//                - 교육시작일자 : 신청일자
//                - 교육종료일자 : 과정의 교육일수로 계산
//            2. 상시교육이 아닌 경우
//                - 교육시작일자 : 개설과정의 교육시작일
//                - 교육종료일자 : 개설과정의 교육종료일
//        */
//        if (course.getIsAlways().equals("1")) {     // 상시교육일 경우
//            fromDate = DateUtil.getTodayString();
//            toDate = DateUtil.getStringDateAddDay(fromDate, course.getDay());
//        } else {    // 상시교육이 아닌 경우
//            fromDate = course.getFromDate();
//            toDate = course.getToDate();
//        }
//        courseAccount.setFromDate(fromDate);
//        courseAccount.setToDate(toDate);
//
//        // 결재자수 Max 설정
//        if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
//            courseAccount.setFFinalCount(2);
//            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//        } else if(isAppr1.equals("Y")) {
//            courseAccount.setFFinalCount(1);
//            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//        } else {
//            courseAccount.setFFinalCount(0);
//            courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
//            courseAccount.setFStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
//        }
//
//        // 교육 대상자 지정인 경우는 상태를 신청 대기상태로 만든다
//        if (requestType.equals("0"))
//            courseAccount.setCourseStatus(CourseStepStatus.none);
//        else {  // 교육 대상자 신청인 경우는 상태를 신청상태로 변경한다.
//            courseAccount.setCourseStatus(CourseStepStatus.request);
//        }
//
//
//        // 과정 신청자 생성
//        CourseAccount saveCourseAccount = courseAccountService.save(courseAccount);
//
//        // 과정 생성
//        // TODO : 교육신청시 교육과정 생성 유무
//
//        // 강의 생성
//        if(courseAccount.getCourse().getSections().size() > 0) {
//            for (CourseSection courseSection : courseAccount.getCourse().getSections()) {
//                CourseSectionAction courseSectionAction = new CourseSectionAction();
//                courseSectionAction.setCourseAccount(saveCourseAccount);
//                courseSectionAction.setAccount(saveCourseAccount.getAccount());
//                courseSectionAction.setCourseSection(courseSection);
//                courseSectionAction.setTotalUseSecond(0);
//                courseSectionAction.setRunCount(0);
//                courseSectionAction.setStatus(SectionStatusType.REQUEST);
//                CourseSectionAction courseSectionAction1 = sectionActionService.save(courseSectionAction);
//            }
//        }
//
//        // 시험 생성
//        if(saveCourseAccount.getCourse().getIsQuiz().equals("Y") && saveCourseAccount.getCourse().getQuizzes().size() > 0) {
//            for (CourseQuiz courseQuiz : saveCourseAccount.getCourse().getQuizzes()) {
//                CourseQuizAction courseQuizAction = new CourseQuizAction();
//                courseQuizAction.setCourseAccount(saveCourseAccount);
//                courseQuizAction.setAccount(courseAccount.getAccount());
//                courseQuizAction.setQuiz(courseQuiz);
//                courseQuizAction.setTotalUseSecond(0);
//                courseQuizAction.setRunCount(0);
//                courseQuizAction.setQuestionCount(courseQuiz.getQuizQuestions().size());
//                courseQuizAction.setStatus(QuizStatusType.REQUEST);
//                quizActionService.saveQuizAction(courseQuizAction);
//            }
//        }
//
//        // 설문 생성
//        if(saveCourseAccount.getCourse().getIsSurvey().equals("Y") && saveCourseAccount.getCourse().getSurveys().size() > 0) {
//            for (CourseSurvey courseSurvey : saveCourseAccount.getCourse().getSurveys()) {
//                CourseSurveyAction courseSurveyAction = new CourseSurveyAction();
//                courseSurveyAction.setCourseAccount(saveCourseAccount);
//                courseSurveyAction.setAccount(courseAccount.getAccount());
//                courseSurveyAction.setCourseSurvey(courseSurvey);
//                courseSurveyAction.setQuestionCount(courseSurvey.getQuestions().size());
//                courseSurveyAction.setStatus(SurveyStatusType.REQUEST);
//                surveyActionService.saveSurveyAction(courseSurveyAction);
//            }
//        }
//
//        return saveCourseAccount;
//    }
//
//        // 4. 부서별 신청
//    public CourseAccount courseAccountDeptProcess(CourseAccount courseAccount) {
//
//        String fromDate;
//        String toDate;
//
//        // 팀장/부서장 승인여부
//        String isAppr1 = courseAccount.getCourse().getCourseMaster().getIsTeamMangerApproval();
//        // 관리자 승인여부
//        String isAppr2 = courseAccount.getCourse().getCourseMaster().getIsCourseMangerApproval();
//
//        // 교육신청
//        courseAccount.setFWdate(DateUtil.getToday());
//        courseAccount.setFStatus("0");
//        courseAccount.setFCurrSeq(1);
//
//
//        /*
//        교육과정에 따른 교육 기간 설정
//            1. 상시 교육
//                - 교육시작일자 : 신청일자
//                - 교육종료일자 : 과정의 교육일수로 계산
//            2. 상시교육이 아닌 경우
//                - 교육시작일자 : 개설과정의 교육시작일
//                - 교육종료일자 : 개설과정의 교육종료일
//        */
//        if (courseAccount.getCourse().getIsAlways().equals("1")) {     // 상시교육일 경우
//            fromDate = DateUtil.getTodayString();
//            toDate = DateUtil.getStringDateAddDay(fromDate, course.getDay());
//        } else {    // 상시교육이 아닌 경우
//            fromDate = courseAccount.getCourse().getFromDate();
//            toDate = courseAccount.getCourse().getToDate();
//        }
//        courseAccount.setFromDate(fromDate);
//        courseAccount.setToDate(toDate);
//
//        // 결재자수 Max 설정
//        if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
//            courseAccount.setFFinalCount(2);
//            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//        } else if(isAppr1.equals("Y")) {
//            courseAccount.setFFinalCount(1);
//            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
//        } else {
//            courseAccount.setFFinalCount(0);
//            courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
//            courseAccount.setFStatus("1");      // 전자결재가 없으면 완료한것으로 처리한다.
//        }
//
//        courseAccount.setCourseStatus(CourseStepStatus.request);
//
//        // 과정 신청자 생성
//        CourseAccount saveCourseAccount = courseAccountService.save(courseAccount);
//
//        // 과정 생성
//        // TODO : 교육신청시 교육과정 생성 유무
//
//        // 강의 생성
//        if(courseAccount.getCourse().getSections().size() > 0) {
//            for (CourseSection courseSection : courseAccount.getCourse().getSections()) {
//                CourseSectionAction courseSectionAction = new CourseSectionAction();
//                courseSectionAction.setCourseAccount(saveCourseAccount);
//                courseSectionAction.setAccount(saveCourseAccount.getAccount());
//                courseSectionAction.setCourseSection(courseSection);
//                courseSectionAction.setTotalUseSecond(0);
//                courseSectionAction.setRunCount(0);
//                courseSectionAction.setStatus(SectionStatusType.REQUEST);
//                CourseSectionAction courseSectionAction1 = sectionActionService.save(courseSectionAction);
//            }
//        }
//
//        // 시험 생성
//        if(saveCourseAccount.getCourse().getIsQuiz().equals("Y") && saveCourseAccount.getCourse().getQuizzes().size() > 0) {
//            for (CourseQuiz courseQuiz : saveCourseAccount.getCourse().getQuizzes()) {
//                CourseQuizAction courseQuizAction = new CourseQuizAction();
//                courseQuizAction.setCourseAccount(saveCourseAccount);
//                courseQuizAction.setAccount(courseAccount.getAccount());
//                courseQuizAction.setQuiz(courseQuiz);
//                courseQuizAction.setTotalUseSecond(0);
//                courseQuizAction.setRunCount(0);
//                courseQuizAction.setQuestionCount(courseQuiz.getQuizQuestions().size());
//                courseQuizAction.setStatus(QuizStatusType.REQUEST);
//                quizActionService.saveQuizAction(courseQuizAction);
//            }
//        }
//
//        // 설문 생성
//        if(saveCourseAccount.getCourse().getIsSurvey().equals("Y") && saveCourseAccount.getCourse().getSurveys().size() > 0) {
//            for (CourseSurvey courseSurvey : saveCourseAccount.getCourse().getSurveys()) {
//                CourseSurveyAction courseSurveyAction = new CourseSurveyAction();
//                courseSurveyAction.setCourseAccount(saveCourseAccount);
//                courseSurveyAction.setAccount(courseAccount.getAccount());
//                courseSurveyAction.setCourseSurvey(courseSurvey);
//                courseSurveyAction.setQuestionCount(courseSurvey.getQuestions().size());
//                courseSurveyAction.setStatus(SurveyStatusType.REQUEST);
//                surveyActionService.saveSurveyAction(courseSurveyAction);
//            }
//        }
//
//        return saveCourseAccount;
//    }


    // 과정별 교육대상자 생성
    public CourseAccount courseAccountProcess(Account account, Course course, String requestType) {

        String fromDate;
        String toDate;

        // 팀장/부서장 승인여부
        String isAppr1 = course.getCourseMaster().getIsTeamMangerApproval();
        // 관리자 승인여부
        String isAppr2 = course.getCourseMaster().getIsCourseMangerApproval();

        // 교육신청
        CourseAccount courseAccount = new CourseAccount();
        courseAccount.setCourse(course);
        courseAccount.setAccount(account);
        courseAccount.setRequestDate(DateUtil.getTodayString());
        courseAccount.setFnWdate(DateUtil.getToday());
        courseAccount.setRequestType(requestType);        // 교육신청(0:관리자지정, 1:사용자 신청)
        courseAccount.setFnStatus("0");
        courseAccount.setFnCurrSeq(1);


        /*
        교육과정에 따른 교육 기간 설정
            1. 상시 교육
                - 교육시작일자 : 신청일자
                - 교육종료일자 : 과정의 교육일수로 계산
            2. 상시교육이 아닌 경우
                - 교육시작일자 : 개설과정의 교육시작일
                - 교육종료일자 : 개설과정의 교육종료일
        */
        if (course.getIsAlways().equals("1")) {     // 상시교육일 경우
            fromDate = DateUtil.getTodayString();
            toDate = DateUtil.getStringDateAddDay(fromDate, course.getDay());
        } else {    // 상시교육이 아닌 경우
            fromDate = course.getFromDate();
            toDate = course.getToDate();
        }
        courseAccount.setFromDate(fromDate);
        courseAccount.setToDate(toDate);

        // 결재자수 Max 설정
        if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
            courseAccount.setFnFinalCount(2);
            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
        } else if(isAppr1.equals("Y")) {
            courseAccount.setFnFinalCount(1);
            courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
        } else {
            courseAccount.setFnFinalCount(0);
            courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
            courseAccount.setFnStatus("9");      // 전자결재가 없으면 미진행(9)로 처리한다.
        }

        // 교육 대상자 신청인 경우는 상태를 신청상태로 변경한다.
        courseAccount.setCourseStatus(CourseStepStatus.request);

        // self 교육은 신청시 교육상태로 변경
        if (course.getCourseMaster().getId().equals("BC0101")) {  // self
            courseAccount.setCourseStatus(CourseStepStatus.process);
        }

        // 교육 종결
        return courseAccountService.save(courseAccount);
    }


    // 교육 신청 처리
    public boolean courseRequestProcess(Account account, Course course, String requestType) {

        // 팀장/부서장 승인여부
        String isAppr1 = course.getCourseMaster().getIsTeamMangerApproval();
        // 관리자 승인여부
        String isAppr2 = course.getCourseMaster().getIsCourseMangerApproval();

        // 과정 신청에 필요한 기본 검증을 진행한다.
        // 0: 계정이 존재하지 않음
        // 1: 상위결재권자가 지정되지 않음
        // 2: 관리자가 지정되지 않음
        // 9: 과정 신청 가능
//        if (courseAccountService.accountVerification(account.getUserId()) != 9) return false;

        // 교육과정 대상자 지정인 경우는 대상자만 생성한후 프로세스를 종료한다.
        if (requestType.equals("0")) {
            String fromDate = "";
            String toDate = "";

            // 교육신청
            CourseAccount courseAccount = new CourseAccount();
            courseAccount.setCourse(course);
            courseAccount.setAccount(account);
            courseAccount.setRequestDate("");
            courseAccount.setRequestType(requestType);        // 교육신청(0:관리자지정, 1:사용자 신청)
            courseAccount.setFnStatus("9");
            courseAccount.setFnCurrSeq(1);
            courseAccount.setFromDate("");
            courseAccount.setToDate("");
            courseAccount.setCourseStatus(CourseStepStatus.none);

            // 결재자수 Max 설정
            if(isAppr1.equals("Y") && isAppr2.equals("Y")) {
                courseAccount.setFnFinalCount(2);
                courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
            } else if(isAppr1.equals("Y")) {
                courseAccount.setFnFinalCount(1);
                courseAccount.setIsApproval("1");   // 전자결재유무 0:없음, 1:있음
            } else {
                courseAccount.setFnFinalCount(0);
                courseAccount.setIsApproval("0");   // 전자결재유무 0:없음, 1:있음
            }

            courseAccount.setFnStatus("9");      // 신청전까지는 미진행으로 처리


            // self 교육은 참석처리를 하지 않기 때문에 미리 처리한다.
            // 외부교육은 교육참석보고서 승인시 참석처리를 자동으로 해야 한다.
            // class 교육 및 부서별교육은 참석처리를 관리자가 직접 한다.
            if (course.getCourseMaster().getId().equals("BC0101")) {  // self
                courseAccount.setIsAttendance("1"); // 교육참석유무(0:미참석, 1:참석) => 기본값 0
            }

            // 외부교육이면 isReport(교육보고서 작성유무) 를 1로 설정한다.
            if (course.getCourseMaster().getId().equals("BC0104")) {  // self
                courseAccount.setIsReport("1");
            }

            if (course.getIsAlways().equals("0")) {     // 상시교육이 아닌경우는 과정 교육일자를 기준으로 교육일자를 생성한다.
                fromDate = course.getFromDate();
                toDate = course.getToDate();
            }

            courseAccount.setFromDate(fromDate);
            courseAccount.setToDate(toDate);


            // 부서별 교육은 교육신청 프로세스가 없음으로 바로 교육 완료 상태로 변경한다.
            if(course.getCourseMaster().getId().equalsIgnoreCase("BC0103")) {
                courseAccount.setCourseStatus(CourseStepStatus.wait);
            }


            //courseAccount = courseAccountService.save(courseAccount);
            // 교육 과정 생성
            createUserCourse(courseAccountService.save(courseAccount));

            // 알람 및 메세지 전송
//            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.CourseAccountAssign, account, course, "<a href='http://lms.dtnsm.com/mypage/main'>교육현황 바로가기</a>");

            MessageSource messageSource = MessageSource.builder()
                    .alarmGubun(LmsAlarmGubun.INFO)
                    .lmsAlarmCourseType(LmsAlarmCourseType.CourseAccountAssign)
                    .sender(courseManagerService.getCourseManager().getAccount())
                    .receive(account)
                    .course(course)
                    .title("교육을 신청하세요")
                    .subject(String.format("%s 과정 교육 수강생으로 지정되셨습니다.", course.getTitle()))
                    .content("교육을 신청해 주세요.(<a href='http://lms.dtnsm.com/mypage/main'>교육현황 바로가기</a>)")
                    .build();
            MessageUtil.sendNotificationMessage(messageSource, true);


        } else {
            CourseAccount saveCourseAccount = courseAccountService.getByCourseIdAndUserId(course.getId(), account.getUserId());

            if (saveCourseAccount == null) {
                saveCourseAccount = courseAccountProcess(account, course, requestType);
                // 교육 과정 생성
                createUserCourse(saveCourseAccount);
            }
            else {  // 상시교육은 신청 시점에 교육일을 생성한다.
                // 상시
                if (course.getIsAlways().equals("1")) {     // 상시교육일 경우
                    saveCourseAccount.setFromDate(DateUtil.getTodayString());
                    saveCourseAccount.setToDate(DateUtil.getStringDateAddDay(DateUtil.getTodayString(), course.getDay()));
                }
            }

            saveCourseAccount.setFnWdate(DateUtil.getTodayDate());
            saveCourseAccount.setRequestDate(DateUtil.getTodayString());
            saveCourseAccount.setRequestType(requestType);

            // 외부 교육인 경우는 외부교육참석보고서를 교육완료일 기준 3일이내 작성해야한다.
            if (course.getCourseMaster().getId().equals("BC0104")) {  // 외부교육
                // 보고서 작성 유무
                saveCourseAccount.setIsReport("1");
            }

//            if (course.getIsAlways().equals("1")) {     // 상시교육일 경우는 신청시점부터 교육일자를 산정하여 생성한다.
//                fromDate = DateUtil.getTodayString();
//                toDate = DateUtil.getStringDateAddDay(fromDate, course.getDay());
//            }
//
//            saveCourseAccount.setFromDate(fromDate);
//            saveCourseAccount.setToDate(toDate);


//            // 교육 신청인 경우 sleft 교육인 경우는 교육 참석유무를 참석으로 하고 교육상태를 진행상태로 변경한다.
//            if (course.getCourseMaster().getId().equals("BC0101")) {  // self
//                courseAccount = courseAccountSelfProcess(courseAccount);
//            } else if (course.getCourseMaster().getId().equals("BC0102")) {  // class 교육
//                courseAccount = courseAccountClassProcess(courseAccount);
//            } else if (course.getCourseMaster().getId().equals("BC0104")) {  // 외부교육
//                courseAccount = courseAccountOtherProcess(courseAccount);
//            } else if (course.getCourseMaster().getId().equals("BC0103")) {  // 부서별교육
//                courseAccount = courseAccountDeptProcess(courseAccount);
//            }

            // 전자결재가 있는 경우
            if (saveCourseAccount.getIsApproval().equals("1")) {

                // 전자결재가 있는경우 진행 상태로 변경한다.
                saveCourseAccount.setFnStatus("0");

                // 내결재사항을 추가한다.
                CourseAccountOrder courseAccountOrder = new CourseAccountOrder();
                courseAccountOrder.setFnUser(account);
                courseAccountOrder.setSignature(signatureService.getSign(account.getUserId()));
                courseAccountOrder.setCourseAccount(saveCourseAccount);

                // fKind : 0:초기, 1:결재(팀장), 2. 합의(관리자)
                courseAccountOrder.setFnKind("1");
                courseAccountOrder.setFnStatus("1");
                courseAccountOrder.setFnSeq(0);
                courseAccountOrder.setFnNext("0");
                courseAccountOrder.setFnDate(DateUtil.getToday());
                courseAccountOrder.setFnComment("");
                courseAccountOrderService.save(courseAccountOrder);

                //courseAccount.addCourseAccountOrder(courseAccountOrderService.save(courseAccountOrder1));

                Account apprAccount;
                if (isAppr1.equals("Y")) {
                    Account apprAccount2 = userService.getAccountByUserId(account.getParentUserId());
                    CourseAccountOrder courseAccountOrder2 = new CourseAccountOrder();
                    courseAccountOrder2.setFnUser(apprAccount2);
                    courseAccountOrder2.setCourseAccount(saveCourseAccount);
                    // fKind : 0:초기, 1:결재(팀장), 2. 합의(관리자)
                    courseAccountOrder2.setFnKind("1");
                    courseAccountOrder2.setFnStatus("0");
                    courseAccountOrder2.setFnSeq(1);
                    courseAccountOrder2.setFnNext("1");

                    courseAccountOrderService.save(courseAccountOrder2);

                    //courseAccount.addCourseAccountOrder(courseAccountOrderService.save(courseAccountOrder1));

                    // 알람 및 메세지 전송

                    MessageSource messageSource = MessageSource.builder()
                            .alarmGubun(LmsAlarmGubun.INFO)
                            .lmsAlarmCourseType(LmsAlarmCourseType.Request)
                            .sender(account)
                            .receive(apprAccount2)
                            .course(course)
                            .title("교육결재 요청")
                            .subject(String.format("%s가 교육신청 결재를 요청하였습니다.", account.getName()))
                            .content("교육신청 결재 요청 (<a href='http://lms.dtnsm.com/approval/mainApproval?status=request'>결재함 바로가기</a>)")
                            .build();

                    MessageUtil.sendNotificationMessage(messageSource, true);

//                    MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Request, apprAccount2, course
//                            , String.format("%s로 부터 교육신청 결재가 요청되었습니다." ,account.getName()));
                }

                if (isAppr2.equals("Y")) {
                    Account apprAccount3 = userService.getAccountByUserId(courseManagerService.getCourseManager().getUserId());
                    CourseAccountOrder courseAccountOrder3 = new CourseAccountOrder();
                    courseAccountOrder3.setFnUser(apprAccount3);
                    courseAccountOrder3.setCourseAccount(saveCourseAccount);
                    // fKind : 0:초기, 1:결재(팀장), 2. 합의(관리자)
                    courseAccountOrder3.setFnKind("2");
                    courseAccountOrder3.setFnStatus("0");
                    courseAccountOrder3.setFnSeq(2);

                    courseAccountOrderService.save(courseAccountOrder3);
                }

                // 전자결재가 있는 경우는 교육상태를 request 상태로 변경한다.
                saveCourseAccount.setCourseStatus(CourseStepStatus.request);

            } else {    // 전자결재가 없는 경우 교육을 진행상태로 변경한다.
                saveCourseAccount.setCourseStatus(CourseStepStatus.process);

                // self 교육이고 전자결재가 없고 (시험, 설문)이 없으면 교육을 종결시킨다.
//                if (course.getCourseMaster().getId().equals("BC0101") && course.getSections() != null && course.getIsQuiz().equals("N") && course.getIsSurvey().equals("N")) {
//                    saveCourseAccount.setCourseStatus(CourseStepStatus.complete);
//                    saveCourseAccount.setIsAttendance("1");
//                    saveCourseAccount.setIsCommit("1");
//
//                    // 수료증 발급여부가 Y이면 발행한다.
//                    if (course.getIsCerti().equals("Y")) {
//                        String certificateNo = courseCertificateService.newCertificateNumber(saveCourseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), saveCourseAccount).getFullNumber();
//                        saveCourseAccount.setCertificateNo(certificateNo);
//                    }
//                }
            }

            courseAccountService.save(saveCourseAccount);
        }

       // 과정 결재 정보 생성
        //CourseAccount courseAccount1 = courseAccountService.save(courseAccount);



        // 사용자별 과정 생성
        /*
            self-training	N	N	BC0101
            class training	Y	Y	BC0102
            부서별 교육	N	Y	BC0102
            외부 교육	N	Y	BC0101
        */
        // self 교육인 아닌 경우 교육 사전 처리를 진행한다.
//        if (!saveCourseAccount.getCourse().getCourseMaster().equals("BC0101")) {
//
//            // 1.강의 처리(self 교육이외에는 실제 강의가 없음)
//           for(CourseSectionAction courseSectionAction : saveCourseAccount.getCourseSectionActions()) {
//               // 강의를 종료 시킨다.
//               courseSectionAction.setStatus(SectionStatusType.COMPLETE);
//               courseSectionActionService.save(courseSectionAction);
//           }
//
//            // 2.시험도 없고 설문도 없는 경우 교육 완료 처리
//            if(course.getIsQuiz().equals("N") && course.getIsSurvey().equals("N")) {
//                saveCourseAccount.setCourseStatus(CourseStepStatus.complete);
//
//                courseAccountService.save(saveCourseAccount);
//            }
//        }

        return true;
    }


    // 교육과정 생성
    public void createUserCourse(CourseAccount courseAccount) {
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
//                courseSectionAction.setFromDate(fromDate);  // 개인별 교육기간 설정
//                courseSectionAction.setToDate(toDate);      // 개인별 교육기간 설정
                courseSectionAction.setStatus(SectionStatusType.REQUEST);
                CourseSectionAction courseSectionAction1 = sectionActionService.save(courseSectionAction);

                // self외에는 status를 complete로 변경한다.
                if (!courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {
                    sectionActionService.UpdateCourseSectionActionComplete(courseSectionAction1);
                }
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
//                courseQuizAction.setFromDate(fromDate);  // 개인별 교육기간 설정
//                courseQuizAction.setToDate(toDate);      // 개인별 교육기간 설정
                courseQuizAction.setQuestionCount(courseQuiz.getQuizQuestions().size());
                courseQuizAction.setStatus(QuizStatusType.REQUEST);

                // self외에는 실제 강의가 없기 때문에 상태를 ongoing 상태로 변경한다.
                if (!courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {
                    courseQuizAction.setStatus(QuizStatusType.ONGOING);
                }

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
//                courseSurveyAction.setFromDate(fromDate);  // 개인별 교육기간 설정
//                courseSurveyAction.setToDate(toDate);      // 개인별 교육기간 설정
                courseSurveyAction.setQuestionCount(courseSurvey.getQuestions().size());
                courseSurveyAction.setStatus(SurveyStatusType.REQUEST);

                // self외에는 시험이 없는 경우 onGoing 상태로 변경한다
                if (!courseAccount.getCourse().getCourseMaster().getId().equals("BC0101") && courseAccount.getCourse().getIsQuiz().equals("N")) {
                    courseSurveyAction.setStatus(SurveyStatusType.ONGOING);
                }

                surveyActionService.saveSurveyAction(courseSurveyAction);
            }
        }

        // 알림 등록
    }


    // 교육 1차 승인 처리
    public void courseApproval1Proces(CourseAccountOrder courseAccountOrder) {

        int finalCount = courseAccountOrder.getCourseAccount().getFnFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFnUser().getUserId()));
        courseAccountOrder.setFnDate(DateUtil.getToday());
        courseAccountOrder.setFnNext("0");
        courseAccountOrder.setFnStatus("1");
        courseAccountOrder.setFnComment("");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        if(finalCount == courseAccountOrder.getFnSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            courseAccount.setFnStatus("1");
            courseAccount.setCourseStatus(CourseStepStatus.process);

            CourseAccount courseAccount1 = courseAccountService.save(courseAccount);

            // 최종 승인이면 기안자에게 메일 전송
//            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, courseAccount1.getAccount(), courseAccount1.getCourse());

            MessageSource messageSource = MessageSource.builder()
                    .alarmGubun(LmsAlarmGubun.INFO)
                    .lmsAlarmCourseType(LmsAlarmCourseType.Approval)
                    .sender(courseAccountOrder.getFnUser()) // 승인자
                    .receive(courseAccount1.getAccount())
                    .course(courseAccount1.getCourse())
                    .title("교육결재 요청")
                    .subject(String.format("%s님의 %s 과정 교육신청이 최종승인 되었습니다.", courseAccount1.getAccount().getName(), courseAccount1.getCourse().getTitle()))
                    .content(String.format("교육신청 결재 완료 (<a href='http://lms.dtnsm.com/approval/mainRequest%s?status=complete'>결재완료 바로가기</a>)"
                            , courseAccount1.getCourse().getCourseMaster().getId().equals("BC0104") ? "2" : "1"))
                    .build();

            MessageUtil.sendNotificationMessage(messageSource, true);

            messageSource = MessageSource.builder()
                    .alarmGubun(LmsAlarmGubun.INFO)
                    .lmsAlarmCourseType(LmsAlarmCourseType.Approval)
                    .sender(courseAccountOrder.getFnUser()) // 승인자
                    .receive(courseManagerService.getCourseManager().getAccount())
                    .course(courseAccount1.getCourse())
                    .title("교육결재 요청")
                    .subject(String.format("%s님의 %s 과정 교육신청이 최종승인 되었습니다.", courseAccount1.getAccount().getName(), courseAccount1.getCourse().getTitle()))
                    .content("교육신청 결재 완료")
                    .build();

            MessageUtil.sendNotificationMessage(messageSource, true);

        } else {
            CourseAccountOrder nextOrder = courseAccountOrderService.getByFnoAndSeq(courseAccountOrder.getCourseAccount().getId(), courseAccountOrder.getFnSeq()+1);
            if (nextOrder != null) {

                nextOrder.setFnNext("1");
                nextOrder = courseAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                courseAccount.setFnCurrSeq(nextOrder.getFnSeq());
                courseAccountService.save(courseAccount);

                // 알람 및 메세지 전송
//                MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, nextOrder.getFnUser(), courseAccount.getCourse());

                MessageSource messageSource = MessageSource.builder()
                        .alarmGubun(LmsAlarmGubun.INFO)
                        .lmsAlarmCourseType(LmsAlarmCourseType.Approval)
                        .sender(courseAccountOrder.getFnUser()) // 승인자
                        .receive(nextOrder.getFnUser())
                        .course(courseAccount.getCourse())
                        .title("교육결재 요청")
                        .subject(String.format("%s님의 %s 과정 교육신청결재 문서가 있습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                        .content("교육신청 결재 요청 (<a href='http://lms.dtnsm.com/approval/mainApproval?status=request'>결재함 바로가기</a>)")
                        .build();

                MessageUtil.sendNotificationMessage(messageSource, true);
            }
        }
    }

    // 교육 1차 기각 처리
    public void courseReject1Proces(CourseAccountOrder courseAccountOrder) {

        int finalCount = courseAccountOrder.getCourseAccount().getFnFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFnUser().getUserId()));
        courseAccountOrder.setFnDate(DateUtil.getToday());
        courseAccountOrder.setFnNext("0");
        courseAccountOrder.setFnStatus("2");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        courseAccount.setFnStatus("2");
        courseAccount.setCourseStatus(CourseStepStatus.reject);
        courseAccount.setIsCommit("1");

        courseAccountService.save(courseAccount);

        // 알람 및 메세지 전송
//        MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Reject, courseAccount.getAccount(), courseAccount.getCourse());

        // 기안자 발송
        MessageSource messageSource = MessageSource.builder()
                .alarmGubun(LmsAlarmGubun.INFO)
                .lmsAlarmCourseType(LmsAlarmCourseType.Reject)
                .sender(courseAccountOrder.getFnUser()) // 기각자
                .receive(courseAccount.getAccount())
                .course(courseAccount.getCourse())
                .title("교육결재 요청")
                .subject(String.format("%s님의 %s 과정 교육신청이 반려 처리 되었습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                .content(String.format("교육신청 결재 반려 (<a href='http://lms.dtnsm.com/approval/mainRequest%s?status=reject'>반려함 바로가기</a>)"
                        , courseAccount.getCourse().getCourseMaster().getId().equals("BC0104") ? "2" : "1"))
                .build();

        MessageUtil.sendNotificationMessage(messageSource, true);

        // 관리자 발송
        messageSource = MessageSource.builder()
                .alarmGubun(LmsAlarmGubun.INFO)
                .lmsAlarmCourseType(LmsAlarmCourseType.Reject)
                .sender(courseAccountOrder.getFnUser()) // 기각자
                .receive(courseManagerService.getCourseManager().getAccount())
                .course(courseAccount.getCourse())
                .title("교육신청 결재 반려")
                .subject(String.format("%s님의 %s 과정 교육신청이 반려 처리 되었습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                .content(String.format("교육신청 결재 반려"))
                .build();

        MessageUtil.sendNotificationMessage(messageSource, true);

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

        int finalCount = courseAccountOrder.getCourseAccount().getFnFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFnUser().getUserId()));
        courseAccountOrder.setFnDate(DateUtil.getToday());
        courseAccountOrder.setFnNext("0");
        courseAccountOrder.setFnStatus("1");
        courseAccountOrder.setFnComment("");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        if(finalCount == courseAccountOrder.getFnSeq()) {   // 종결처리
            //  승인: 1, 기각 : 2
            courseAccount.setFnStatus("1");
            courseAccount.setCourseStatus(CourseStepStatus.process);

            courseAccount = courseAccountService.save(courseAccount);


            // 최종 승인이면 기안자에게 메일 전송
            MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, courseAccount.getAccount(), courseAccount.getCourse());

            // 기안자 발송
            MessageSource messageSource = MessageSource.builder()
                    .alarmGubun(LmsAlarmGubun.INFO)
                    .lmsAlarmCourseType(LmsAlarmCourseType.Approval)
                    .sender(courseAccountOrder.getFnUser()) // 승인자
                    .receive(courseAccount.getAccount())
                    .course(courseAccount.getCourse())
                    .title("교육결재 요청")
                    .subject(String.format("%s님의 %s 과정 교육신청이 최종승인 되었습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                    .content(String.format("교육신청 결재 완료 (<a href='http://lms.dtnsm.com/approval/mainRequest%s?status=complete'>결재완료 바로가기</a>)"
                            , courseAccount.getCourse().getCourseMaster().getId().equals("BC0104") ? "2" : "1"))
                    .build();

            MessageUtil.sendNotificationMessage(messageSource, true);

            messageSource = MessageSource.builder()
                    .alarmGubun(LmsAlarmGubun.INFO)
                    .lmsAlarmCourseType(LmsAlarmCourseType.Approval)
                    .sender(courseAccountOrder.getFnUser()) // 승인자
                    .receive(courseManagerService.getCourseManager().getAccount())
                    .course(courseAccount.getCourse())
                    .title("교육결재 요청")
                    .subject(String.format("%s님의 %s 과정 교육신청이 최종승인 되었습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                    .content("교육신청 결재 완료")
                    .build();

            MessageUtil.sendNotificationMessage(messageSource, true);

        } else {

            CourseAccountOrder nextOrder = courseAccountOrderService.getByFnoAndSeq(courseAccountOrder.getCourseAccount().getId(), courseAccountOrder.getFnSeq()+1);
            if (nextOrder != null) {
                nextOrder.setFnNext("1");
                nextOrder = courseAccountOrderService.save(nextOrder);

                // 마스터에 다음 결재자 순번을 업데이트 한다.
                courseAccount.setFnCurrSeq(nextOrder.getFnSeq());
                courseAccountService.save(courseAccount);

                // 알람 및 메세지 전송
//                MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Approval, nextOrder.getFnUser(), courseAccount.getCourse());

                MessageSource messageSource = MessageSource.builder()
                        .alarmGubun(LmsAlarmGubun.INFO)
                        .lmsAlarmCourseType(LmsAlarmCourseType.Approval)
                        .sender(courseAccountOrder.getFnUser()) // 승인자
                        .receive(nextOrder.getFnUser())
                        .course(courseAccount.getCourse())
                        .title("교육결재 요청")
                        .subject(String.format("%s님의 %s 과정 교육신청결재 문서가 있습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                        .content("교육신청 결재 요청 (<a href='http://lms.dtnsm.com/approval/mainApproval?status=request'>결재함 바로가기</a>)")
                        .build();

                MessageUtil.sendNotificationMessage(messageSource, true);
            }
        }
    }

    // 교육 2차 기각 처리
    public void courseReject2Proces(CourseAccountOrder courseAccountOrder) {

        int finalCount = courseAccountOrder.getCourseAccount().getFnFinalCount();

        courseAccountOrder.setSignature(signatureService.getSign(courseAccountOrder.getFnUser().getUserId()));
        courseAccountOrder.setFnDate(DateUtil.getToday());
        courseAccountOrder.setFnNext("0");
        courseAccountOrder.setFnStatus("2");

        courseAccountOrder = courseAccountOrderService.save(courseAccountOrder);

        CourseAccount courseAccount = courseAccountOrder.getCourseAccount();

        courseAccount.setFnStatus("2");
        courseAccount.setCourseStatus(CourseStepStatus.reject);
        courseAccount.setIsCommit("1");

        courseAccountService.save(courseAccount);

        // 기안자에게 알람 및 메세지 전송
//        MessageUtil.sendNotificationMessage(LmsAlarmCourseType.Reject, courseAccount.getAccount(), courseAccount.getCourse());

        // 기안자 발송
        MessageSource messageSource = MessageSource.builder()
                .alarmGubun(LmsAlarmGubun.INFO)
                .lmsAlarmCourseType(LmsAlarmCourseType.Reject)
                .sender(courseAccountOrder.getFnUser()) // 기각자
                .receive(courseAccount.getAccount())
                .course(courseAccount.getCourse())
                .title("교육신청 결재 반려")
                .subject(String.format("%s님의 %s 과정 교육신청이 반려 처리 되었습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                .content(String.format("교육신청 결재 반려 (<a href='http://lms.dtnsm.com/approval/mainRequest%s?status=reject'>반려함 바로가기</a>)"
                        , courseAccount.getCourse().getCourseMaster().getId().equals("BC0104") ? "2" : "1"))
                .build();

        MessageUtil.sendNotificationMessage(messageSource, true);

        // 관리자 발송
        messageSource = MessageSource.builder()
                .alarmGubun(LmsAlarmGubun.INFO)
                .lmsAlarmCourseType(LmsAlarmCourseType.Reject)
                .sender(courseAccountOrder.getFnUser()) // 기각자
                .receive(courseManagerService.getCourseManager().getAccount())
                .course(courseAccount.getCourse())
                .title("교육신청 결재 반려")
                .subject(String.format("%s님의 %s 과정 교육신청이 반려 처리 되었습니다.", courseAccount.getAccount().getName(), courseAccount.getCourse().getTitle()))
                .content(String.format("교육신청 결재 반려"))
                .build();

        MessageUtil.sendNotificationMessage(messageSource, true);


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





//    public void sendNotificate(Account account, Course course, LmsAlarmCourseType lmsAlarmCourseType) {
//        Mail mail = new Mail();
//        mail.setEmail(account.getEmail());      // Email
//        mail.setObject(course.getTitle());      // Subject
//        mail.setMessage(course.getContent());   // Content
//        mailService.send(mail, lmsAlarmCourseType);
//    }
//
//    // 교육과정 메일 전송
//    public void sendMail(Account account, Course course, LmsAlarmCourseType lmsAlarmCourseType) {
//        Mail mail = new Mail();
//        mail.setEmail(account.getEmail());      // Email
//        mail.setObject(course.getTitle());      // Subject
//        mail.setMessage(course.getContent());   // Content
//        mailService.send(mail, lmsAlarmCourseType);
//    }
}

