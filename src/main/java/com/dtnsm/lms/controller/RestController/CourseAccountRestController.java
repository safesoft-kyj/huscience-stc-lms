package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.service.CourseAccountService;
import com.dtnsm.lms.service.CourseService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/api/courseAccount")
public class CourseAccountRestController {

    @Autowired
    CourseAccountService courseAccountService;

    @Autowired
    CourseService courseService;

    // 교
    @PostMapping("/isVerificationRequestWait")
    public int isVerificationRequestWait(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        // 과정 신청에 필요한 기본 검증을 진행한다.
        // 0: 계정이 존재하지 않음
        // 1: 상위결재권자가 지정되지 않음
        // 2: 관리자가 지정되지 않음
        // 9: 과정 신청 가능

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserIdAndCourseStatus(courseId, userId, CourseStepStatus.none);

        // 신청대기중인 과정정보가 없는 경우 리턴한다.
        if (courseAccount == null) return 3;

        return courseAccountService.accountVerification(userId);
    }

    @PostMapping("/isVerification")
    public int accountVerification(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        // 과정 신청에 필요한 기본 검증을 진행한다.
        // 0: 계정이 존재하지 않음
        // 1: 상위결재권자가 지정되지 않음
        // 2: 관리자가 지정되지 않음
        // 9: 과정 신청 가능
        // 10: 과정신청기간이 아님

        Course course = courseService.getCourseById(courseId);

        // 과정 신청기간인지를 체크한다.
        if(!DateUtil.isWithinRange(DateUtil.getTodayString(), course.getRequestFromDate(), course.getRequestToDate())) return 10;

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserIdAndRequestType(courseId, userId, "1");

        // 이미 신청정보가 있는 경우는 리턴한다.
        if (courseAccount != null) return 4;

        return courseAccountService.accountVerification(userId);
    }

    @PostMapping("/isVerification2")
    public int accountVerification2(@RequestParam String userId){

        // 과정 신청에 필요한 기본 검증을 진행한다.
        // 0: 계정이 존재하지 않음
        // 1: 상위결재권자가 지정되지 않음
        // 2: 관리자가 지정되지 않음
        // 9: 과정 신청 가능

        return courseAccountService.accountVerification(userId);
    }

    // 사용자가 특정과정을 신청한적이 있는지 체크 (신청한적이 있으면 true, 없으면 false)
    @PostMapping("/isRequest")
    public boolean isRequest(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserIdAndRequestType(courseId, userId, "1");

        return courseAccount == null ? false : true;
    }


    @GetMapping("/isRequest")
    public boolean isRequest2(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserIdAndRequestType(courseId, userId, "1");

        return courseAccount == null ? false : true;
    }

    // 교육필수대상자로 지정된 상태에서 교육신청대기중이 건.
    @GetMapping("/isRequestWait")
    public boolean isRequestWait(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserIdAndCourseStatus(courseId, userId, CourseStepStatus.none);

        return courseAccount == null ? false : true;
    }


    // 두기간사이에 포함되는지 여부
    @GetMapping("/isCoursePeriod")
    public boolean isCoursePeriod(@RequestParam("docId") long docId){

        CourseAccount courseAccount = courseAccountService.getById(docId);
        String today = DateUtil.getTodayString();

        return DateUtil.isWithinRange(today, courseAccount.getFromDate(), courseAccount.getToDate());
    }
}
