package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.*;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@RestController
@RequestMapping("/mypage/api")
public class MypageRestController {

    @Autowired
    CourseSectionService courseSectionService;

    @Autowired
    CourseSectionActionService courseSectionActionService;

    @Autowired
    CourseSurveyActionService courseSurveyActionService;

    @Autowired
    CourseSectionActionHistoryService courseSectionActionHistoryService;

    @Autowired
    CourseQuizService courseQuizService;

    @Autowired
    CourseQuizActionService courseQuizActionService;

    @Autowired
    CourseQuizActionHistoryService courseQuizActionHistoryService;

    @Autowired
    CourseSurveyService courseSurveyService;

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseCertificateService courseCertificateService;

    @Autowired
    BinderLogService binderLogService;

    @Autowired
    UserServiceImpl userService;


    // 2020-05-14 강의를 PDF -> Image로 변경하면서 사용
    @PostMapping("/pdfImageview")
    public boolean pdfImageview(@RequestParam("sectionActionId") long sectionActionId
            , @RequestParam("imageCurrent") int imageCurrent
            , @RequestParam("useSecond") int useSecond
            , @RequestParam("startDate") Date startDate
            , @RequestParam("endDate") Date endDate
            , @RequestParam("endFlag") int endFlag
            , RedirectAttributes attributes){

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        CourseSectionAction courseSectionAction = courseSectionActionService.getById(sectionActionId);

        CourseSectionActionHistory courseSectionActionHistory = new CourseSectionActionHistory();

//        if (courseSectionAction == null) {
//            courseSectionAction = new CourseSectionAction();
//            courseSectionAction.setAccount(account);
//            courseSectionAction.setCourseSection(courseSection);
//            courseSectionAction.setTotalUseSecond(0);
//        }

        int totalSecond = courseSectionAction.getTotalUseSecond() + useSecond;

//        if (totalSecond > (courseSectionAction.getCourseSection().getSecond()) || courseSectionAction.getRunCount() >= 1) {
        // 교육수강을 완료하면 완료 처리한다.(endFlag => 1 계속학습, 2 학습 종료)
        if (endFlag == 2) {
            //totalSecond = courseSectionAction.getCourseSection().getSecond();

            // 수료증 정보 확인
            if(courseSectionAction.getCourseAccount().getCourse().getIsCerti().equals("Y")) {
                if(!courseCertificateService.getCourseCertificateActive()) {
                    attributes.addFlashAttribute("type", "error");
                    attributes.addFlashAttribute("msg", "수료증 정보가 없어 발급할 수 없습니다. 교육관리자에게 문의하세요.");
                    return false;
                }
            }

            courseSectionAction.setStatus(SectionStatusType.COMPLETE);
            courseSectionAction.setExecuteDate(DateUtil.getTodayString());


            //courseSectionActionHistory.setUseSecond(courseSectionAction.getCourseSection().getSecond() - courseSectionAction.getTotalUseSecond());
            courseSectionActionHistory.setUseSecond(useSecond);

        } else {
            courseSectionAction.setStatus(SectionStatusType.ONGOING);
        }

        courseSectionAction.setImageCurrent(imageCurrent);
        courseSectionAction.setRunCount(courseSectionAction.getRunCount() + 1);
        courseSectionAction.setTotalUseSecond(totalSecond);

        CourseSectionAction courseSectionAction1 = courseSectionActionService.save(courseSectionAction);


        // 교육수강이 완료되면 이후 전체 강의 수강이 완료되었는지 확인후
        // 완료 되었다면 시험의 상태를 ONGOING 으로 변경하여 시험에 응시할 수 있도록 한다.
        if (courseSectionAction1.getStatus().equals(SectionStatusType.COMPLETE)) {

            int completeCount = 0;

            // 1. 다른 미이수 강의가 있는지 확인
            CourseAccount courseAccount = courseSectionAction1.getCourseAccount();

            // 교육과정의 퀴즈 여부가 Y인 경우만 실행
            if (courseAccount.getCourseQuizActions().size() > 0) {
//            if (courseAccount.getCourse().getIsQuiz().equals("Y")) {
                for (CourseSectionAction courseSectionAction2 : courseAccount.getCourseSectionActions()) {
                    if (courseSectionAction2.getStatus().equals(SectionStatusType.COMPLETE)) {
                        completeCount++;
                    }
                }

                // 2.  강의수와  COMPLETE 수가 같으면 다음 퀴즈를 ONGOING 상태로 변경한다.
                if (courseAccount.getCourseSectionActions().size() == completeCount) {
                    for (CourseQuizAction courseQuizAction : courseAccount.getCourseQuizActions()) {
                        courseQuizAction.setStatus(QuizStatusType.ONGOING);
                        courseQuizActionService.saveQuizAction(courseQuizAction);
                    }
                }
            } else if (courseAccount.getCourseQuizActions().size() == 0  && courseAccount.getCourseSurveyActions().size() > 0) {
                for (CourseSectionAction courseSectionAction2 : courseAccount.getCourseSectionActions()) {
                    if (courseSectionAction2.getStatus().equals(SectionStatusType.COMPLETE)) {
                        completeCount++;
                    }
                }

                // 2.  강의수와  COMPLETE 수가 같으면 다음 설문을 ONGOING 상태로 변경한다.
                if (courseAccount.getCourseSectionActions().size() == completeCount) {
                    for (CourseSurveyAction courseSurveyAction : courseAccount.getCourseSurveyActions()) {
                        courseSurveyAction.setStatus(SurveyStatusType.ONGOING);
                        courseSurveyActionService.saveSurveyAction(courseSurveyAction);
                    }
                }
            } else {  // CourseAccount 의 상태값을 완료로 변경하고 디지털 바인더 로그를 발생시킨다.

                if (courseAccount.getCourseStatus() != CourseStepStatus.complete) {

                    courseAccount.setCourseStatus(CourseStepStatus.complete);
                    courseAccount.setIsCommit("1");

                    // 수료증 처리
                    if (courseAccount.getCourse().getIsCerti().equals("Y") && !courseAccount.getCourse().getCertiHead().equals("")) {
                        String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                        courseAccount.setCertificateNo(certificateNo);
                    }

                    // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
                    // 강의별로 로그를 생성시킨다.
                    binderLogService.createTrainingLog(courseAccountService.save(courseAccount));

                    // binderLogService.createTrainingLog 에서 처리
//                courseSectionAction1 = courseSectionActionService.getById(sectionActionId);
//                courseSectionAction1.setLogApplyDate(DateUtil.getTodayDate());
//                courseSectionAction1.setIsLogApply("1");
//                courseSectionAction1 = courseSectionActionService.save(courseSectionAction1);
                }
            }
        }

        courseSectionActionHistory.setStartDate(startDate);
        courseSectionActionHistory.setEndDate(endDate);
        courseSectionActionHistory.setUseSecond(useSecond);
        courseSectionActionHistory.setSectionAction(courseSectionAction1);

        courseSectionActionHistoryService.save(courseSectionActionHistory);

        return true;
    }


    // 2020-05-14 강의를 PDF 로 사용(현재 사용하지 않음)
    @PostMapping("/pdfview")
    public boolean treeViewGet(@RequestParam("sectionActionId") long sectionActionId
            , @RequestParam("useSecond") int useSecond
            , @RequestParam("startDate") Date startDate
            , @RequestParam("endDate") Date endDate
            , @RequestParam("endFlag") int endFlag){

        Account account = userService.getAccountByUserId(SessionUtil.getUserId());

        CourseSectionAction courseSectionAction = courseSectionActionService.getById(sectionActionId);

        CourseSectionActionHistory courseSectionActionHistory = new CourseSectionActionHistory();

//        if (courseSectionAction == null) {
//            courseSectionAction = new CourseSectionAction();
//            courseSectionAction.setAccount(account);
//            courseSectionAction.setCourseSection(courseSection);
//            courseSectionAction.setTotalUseSecond(0);
//        }

        int totalSecond = courseSectionAction.getTotalUseSecond() + useSecond;

        courseSectionAction.setRunCount(courseSectionAction.getRunCount() + 1);


//        if (totalSecond > (courseSectionAction.getCourseSection().getSecond()) || courseSectionAction.getRunCount() >= 1) {
        // 교육수강을 완료하면 완료 처리한다.(endFlag => 1 계속학습, 2 학습 종료)
        if (endFlag == 2) {
            //totalSecond = courseSectionAction.getCourseSection().getSecond();

            courseSectionAction.setStatus(SectionStatusType.COMPLETE);

            //courseSectionActionHistory.setUseSecond(courseSectionAction.getCourseSection().getSecond() - courseSectionAction.getTotalUseSecond());
            courseSectionActionHistory.setUseSecond(useSecond);


        } else {
            courseSectionAction.setStatus(SectionStatusType.ONGOING);
        }

        courseSectionAction.setTotalUseSecond(totalSecond);

        CourseSectionAction courseSectionAction1 = courseSectionActionService.save(courseSectionAction);


        // 교육수강이 완료되면 이후 전체 강의 수강이 완료되었는지 확인후
        // 완료 되었다면 시험의 상태를 ONGOING 으로 변경하여 시험에 응시할 수 있도록 한다.
        if (courseSectionAction1.getStatus().equals(SectionStatusType.COMPLETE)) {

            int completeCount = 0;

            // 1. 다른 미이수 강의가 있는지 확인
            CourseAccount courseAccount = courseSectionAction1.getCourseAccount();

            // 교육과정의 퀴즈 여부가 Y인 경우만 실행
            if (courseAccount.getCourse().getIsQuiz().equals("Y")) {

                for (CourseSectionAction courseSectionAction2 : courseAccount.getCourseSectionActions()) {
                    if (courseSectionAction2.getStatus().equals(SectionStatusType.COMPLETE)) {
                        completeCount++;
                    }
                }

                // 2.  강의수와  COMPLETE 수가 같으면 다음 퀴즈를 ONGOING 상태로 변경한다.
                if (courseAccount.getCourseSectionActions().size() == completeCount) {
                    for (CourseQuizAction courseQuizAction : courseAccount.getCourseQuizActions()) {
                        courseQuizAction.setStatus(QuizStatusType.ONGOING);
                        courseQuizActionService.saveQuizAction(courseQuizAction);
                    }
                }
            } else if (courseAccount.getCourse().getIsQuiz().equals("N") && courseAccount.getCourse().getIsSurvey().equals("Y")) {
                for (CourseSectionAction courseSectionAction2 : courseAccount.getCourseSectionActions()) {
                    if (courseSectionAction2.getStatus().equals(SectionStatusType.COMPLETE)) {
                        completeCount++;
                    }
                }

                // 2.  강의수와  COMPLETE 수가 같으면 다음 퀴즈를 ONGOING 상태로 변경한다.
                if (courseAccount.getCourseSectionActions().size() == completeCount) {
                    for (CourseSurveyAction courseSurveyAction : courseAccount.getCourseSurveyActions()) {
                        courseSurveyAction.setStatus(SurveyStatusType.ONGOING);
                        courseSurveyActionService.saveSurveyAction(courseSurveyAction);
                    }
                }
            } else {  // 시험여부가 N인 경우 CourseAccount 의 상태값을 완료로 변경하고 디지털 바인더 로그를 발생시킨다.

                courseAccount.setCourseStatus(CourseStepStatus.complete);
                courseAccount.setIsCommit("1");

                // 수료증 처리
                if(courseAccount.getCourse().getIsCerti().equals("Y") && !courseAccount.getCourse().getCertiHead().equals("")){
                    String certificateNo = courseCertificateService.newCertificateNumber(courseAccount.getCourse().getCertiHead(), DateUtil.getTodayString().substring(0, 4), courseAccount).getFullNumber();
                    courseAccount.setCertificateNo(certificateNo);
                }

                // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
                // 강의별로 로그를 생성시킨다.
                binderLogService.createTrainingLog(courseAccountService.save(courseAccount));

            }
        }

        courseSectionActionHistory.setStartDate(startDate);
        courseSectionActionHistory.setEndDate(endDate);
        courseSectionActionHistory.setUseSecond(useSecond);
        courseSectionActionHistory.setSectionAction(courseSectionAction1);

        courseSectionActionHistoryService.save(courseSectionActionHistory);

        return true;
    }

    @PostMapping("/quizview")
    public boolean quizView(@RequestParam("quizActionId") long quizActionId
            , @RequestParam("useSecond") int useSecond
            , @RequestParam("startDate") Date startDate
            , @RequestParam("endDate") Date endDate){


        CourseQuizAction courseQuizAction = courseQuizActionService.getCourseQuizActionById(quizActionId);
        CourseQuizActionHistory history = new CourseQuizActionHistory();

        int totalSecond = courseQuizAction.getTotalUseSecond() + useSecond;

        if (totalSecond > (courseQuizAction.getQuiz().getSecond())) {
            totalSecond = courseQuizAction.getQuiz().getSecond();

            history.setUseSecond(courseQuizAction.getQuiz().getSecond() - courseQuizAction.getTotalUseSecond());
        }

        courseQuizAction.setTotalUseSecond(totalSecond);
        courseQuizAction.setRunCount(courseQuizAction.getRunCount() + 1);
        courseQuizAction = courseQuizActionService.saveQuizAction(courseQuizAction);

        history.setRunCount(courseQuizAction.getRunCount());
        history.setStartDate(startDate);
        history.setEndDate(endDate);
        history.setUseSecond(useSecond);
        history.setQuizAction(courseQuizAction);

        courseQuizActionHistoryService.save(history);

        return true;
    }

}
