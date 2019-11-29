package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/courseAccount")
public class CourseAccountRestController {

    @Autowired
    CourseAccountService courseAccountService;

    // 사용자가 특정과정을 신청한적이 있는지 체크 (신청한적이 있으면 true, 없으면 false)
    @PostMapping("/isRequest")
    public boolean isRequest(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);

        return courseAccount == null ? false : true;
    }


    @GetMapping("/isRequest")
    public boolean isRequest2(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);

        return courseAccount == null ? false : true;
    }


    @GetMapping("/isCoursePeriod")
    public boolean isCoursePeriod(@RequestParam("docId") long docId){

        CourseAccount courseAccount = courseAccountService.getById(docId);

        String today = DateUtil.getTodayString();

        //앞에 변수가 크면 1, 작으면 -1, 같으면 0
        int s = today.compareTo(courseAccount.getFromDate());
        int e = today.compareTo(courseAccount.getToDate());

        if (s >= 0 && e <= 0  ) return true; // 오늘기준 교육과정기간안에 포함되었을 경우
        else return false;  // 오늘기준 교육과정이 종료된 경우
    }
}
