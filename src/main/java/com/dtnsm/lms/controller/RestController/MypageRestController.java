package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
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

        if (totalSecond > (courseSectionAction.getCourseSection().getSecond())) {
            totalSecond = courseSectionAction.getCourseSection().getSecond();

            courseSectionActionHistory.setUseSecond(courseSectionAction.getCourseSection().getSecond() - courseSectionAction.getTotalUseSecond());
        }

        courseSectionAction.setTotalUseSecond(totalSecond);
        courseSectionAction.setRunCount(courseSectionAction.getRunCount() + 1);
        courseSectionAction = courseSectionActionService.save(courseSectionAction);

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
