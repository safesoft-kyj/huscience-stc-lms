package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SectionStatusType;
import com.dtnsm.lms.service.*;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/mypage/api")
public class MypageRestController {

    @Autowired
    CourseSectionService courseSectionService;

    @Autowired
    CourseSectionActionService courseSectionActionService;

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
    UserServiceImpl userService;



    @PostMapping("/pdfview")
    public boolean treeViewGet(@RequestParam("sectionActionId") long sectionActionId
            , @RequestParam("useSecond") int useSecond
            , @RequestParam("startDate") Date startDate
            , @RequestParam("endDate") Date endDate){

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

        // 교육수강을 완료하면 완료 처리한다.
        if (totalSecond > (courseSectionAction.getCourseSection().getSecond())) {
            totalSecond = courseSectionAction.getCourseSection().getSecond();

            courseSectionAction.setStatus(SectionStatusType.COMPLETE);

            courseSectionActionHistory.setUseSecond(courseSectionAction.getCourseSection().getSecond() - courseSectionAction.getTotalUseSecond());

        } else {
            courseSectionAction.setStatus(SectionStatusType.ONGOING);
        }

        courseSectionAction.setTotalUseSecond(totalSecond);
        courseSectionAction.setRunCount(courseSectionAction.getRunCount() + 1);
        courseSectionAction = courseSectionActionService.save(courseSectionAction);


        // 교육수강이 완료되면 이후 전체 강의 수강이 완료되었는지 확인후
        // 완료 되었다면 시험의 상태를 ONGOING 으로 변경하여 시험에 응시할 수 있도록 한다.
        if (courseSectionAction.getStatus().equals(SectionStatusType.COMPLETE)) {

            int completeCount = 0;

            // 1. 다른 미이수 강의가 있는지 확인
            Course course = courseSectionAction.getCourseSection().getCourse();

            // 교육과정의 퀴즈 여부가 Y인 경우만 실행
            if (course.getIsQuiz().equals("Y")) {

                for (CourseSection courseSection : course.getSections()) {

                    for (CourseSectionAction courseSectionAction1 : courseSection.getCourseSectionActions()) {

                        if (courseSectionAction1.getStatus().equals(SectionStatusType.COMPLETE)) {
                            completeCount++;
                        }
                    }
                }

                // 2.  강의수와  COMPLETE 수가 같으면 다음 퀴즈를 ONGOING 상태로 변경한다.
                if (course.getSections().size() == completeCount) {
                    for (CourseQuiz courseQuiz : course.getQuizzes()) {
                        for (CourseQuizAction courseQuizAction : courseQuiz.getQuizActions()) {
                            courseQuizAction.setStatus(QuizStatusType.ONGOING);
                            courseQuizActionService.saveQuizAction(courseQuizAction);
                        }
                    }
                }
            } else {  // 시험여부가 N인 경우 CourseAccount 의 상태값을 완료로 변경하고 디지털 바인더 로그를 발생시킨다.

                // CourseAccount 상태값 처리
                for(CourseAccount courseAccount : course.getCourseAccountList()) {
                    courseAccount.setCourseStatus(CourseStepStatus.complete);
                    courseAccount.setIsCommit("1");

                    courseAccountService.save(courseAccount);
                }

                //
                //
                //
                //
                // TODO: 2019/11/12 Digital Binder Employee Training Log 처리 -ks Hwang
            }
        }

        courseSectionActionHistory.setStartDate(startDate);
        courseSectionActionHistory.setEndDate(endDate);
        courseSectionActionHistory.setUseSecond(useSecond);
        courseSectionActionHistory.setSectionAction(courseSectionAction);

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


        history.setStartDate(startDate);
        history.setEndDate(endDate);
        history.setUseSecond(useSecond);
        history.setQuizAction(courseQuizAction);

        courseQuizActionHistoryService.save(history);

        return true;
    }

}
