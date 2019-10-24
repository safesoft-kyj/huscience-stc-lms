package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.service.CourseQuizActionService;
import com.dtnsm.lms.service.CourseQuizService;
import com.dtnsm.lms.service.CourseSectionService;
import com.dtnsm.lms.service.CourseSurveyService;
import com.dtnsm.lms.util.DateUtil;
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
    CourseQuizService courseQuizService;

    @Autowired
    CourseSurveyService courseSurveyService;

    @Autowired
    CourseQuizActionService courseQuizActionService;

    @Autowired
    UserServiceImpl userService;



    @PostMapping("/pdfview")
    public boolean treeViewGet(@RequestParam("sectionId") long sectionId
            , @RequestParam("useSecond") int useSecond
            , @RequestParam("startDate") Date startDate
            , @RequestParam("endDate") Date endDate){


        CourseSection courseSection = courseSectionService.getCourseSectionById(sectionId);
        CourseSectionHistory courseSectionHistory = new CourseSectionHistory();
        courseSectionHistory.setStartDate(startDate);
        courseSectionHistory.setEndDate(endDate);
        courseSectionHistory.setUseSecond(useSecond);

        courseSectionHistory.setCourseSection(courseSection);

        int totalSecond = courseSection.getTotalUseSecond() + useSecond;

        if (totalSecond > (courseSection.getSecond())) {
            totalSecond = courseSection.getSecond();

            courseSectionHistory.setUseSecond(courseSection.getSecond() - courseSection.getTotalUseSecond());
        }

        courseSection.setTotalUseSecond(totalSecond);
        courseSection.addCourseSectionHistory(courseSectionHistory);

        courseSectionService.saveSection(courseSection);

        return true;
    }

    @PostMapping("/quizview")
    public boolean quizView(@RequestParam("quizActionId") long quizActionId
            , @RequestParam("useSecond") int useSecond
            , @RequestParam("startDate") Date startDate
            , @RequestParam("endDate") Date endDate){


        CourseQuizAction courseQuizAction = courseQuizActionService.getCourseQuizActionById(quizActionId);

        CourseQuizActionHistory history = new CourseQuizActionHistory();
        history.setStartDate(startDate);
        history.setEndDate(endDate);
        history.setUseSecond(useSecond);

        history.setQuizAction(courseQuizAction);

        int totalSecond = courseQuizAction.getTotalUseSecond() + useSecond;

        if (totalSecond > (courseQuizAction.getSecond())) {
            totalSecond = courseQuizAction.getSecond();

            history.setUseSecond(courseQuizAction.getSecond() - courseQuizAction.getTotalUseSecond());
        }

        courseQuizAction.setTotalUseSecond(totalSecond);
        courseQuizAction.addQuizActionHistory(history);

        courseQuizActionService.saveQuizAction(courseQuizAction);

        return true;
    }

}
