package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.CourseSection;
import com.dtnsm.lms.domain.CourseSectionHistory;
import com.dtnsm.lms.service.CourseSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/mypage/api")
public class MypageRestController {

    @Autowired
    CourseSectionService courseSectionService;

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

        if (totalSecond > (courseSection.getMinute() * 60)) {
            totalSecond = courseSection.getMinute() * 60;
        }

        courseSection.setTotalUseSecond(totalSecond);
        courseSection.addCourseSectionHistory(courseSectionHistory);

        courseSectionService.saveSection(courseSection);

        return true;
    }

}
