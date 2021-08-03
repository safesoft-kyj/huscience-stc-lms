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

    // 사용자 교육기간 연장(연장횟수 1번만 가능)
    @PostMapping("/coursePeriodExtend")
    public boolean coursePeriodExtend(@RequestParam("docId") long docId) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        // self 상시교육이고 기간 연장이 1번 미만인 경우는 연장 가능
        if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")
                && courseAccount.getCourse().getIsAlways().equals("1")
                && courseAccount.getPeriodExtendCount() < 1) {     // 상시교육일 경우
//            courseAccount.setFromDate(DateUtil.getTodayString());
            courseAccount.setToDate(DateUtil.getStringDateAddDay(DateUtil.getTodayString(), courseAccount.getCourse().getDay()));
            courseAccount.setPeriodExtendCount(courseAccount.getPeriodExtendCount() + 1);
            courseAccount.setCourseStatus(CourseStepStatus.process);
            courseAccountService.save(courseAccount);
            return true;
        }

//        if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")) {     // 상시교육일 경우
//            courseAccount.setFromDate(DateUtil.getTodayString());
//            courseAccount.setToDate(DateUtil.getStringDateAddDay(DateUtil.getTodayString(), courseAccount.getCourse().getDay()));
//            courseAccount.setCourseStatus(CourseStepStatus.process);
//            courseAccountService.save(courseAccount);
//            return true;
//        }

        return false;
    }

    // 2020-05-17 관리자 교육기간 연장(연장 횟수 제한 없음)
    @PostMapping("/courseAdminPeriodExtend")
    public boolean courseAdminPeriodExtend(@RequestParam("docId") long docId) {

        CourseAccount courseAccount = courseAccountService.getById(docId);

        // self 상시교육인 경우 연장 가능
        if (courseAccount.getCourse().getCourseMaster().getId().equals("BC0101")
                && courseAccount.getCourse().getIsAlways().equals("1")) {     // 상시교육일 경우
//            courseAccount.setFromDate(DateUtil.getTodayString());
            courseAccount.setToDate(DateUtil.getStringDateAddDay(DateUtil.getTodayString(), courseAccount.getCourse().getDay()));
            courseAccount.setPeriodExtendCount(courseAccount.getPeriodExtendCount() + 1);
            courseAccount.setCourseStatus(CourseStepStatus.process);
            courseAccountService.save(courseAccount);
            return true;
        }

        return false;
    }

    // 교
    @PostMapping("/isVerificationRequestWait")
    public int isVerificationRequestWait(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        // 과정 신청에 필요한 기본 검증을 진행한다.
        // 0: 계정이 존재하지 않음
        // 1: 상위결재권자가 지정되지 않음
        // 2: 관리자가 지정되지 않음
        // 9: 과정 신청 가능

        Course course = courseService.getCourseById(courseId);

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserIdAndCourseStatus(courseId, userId, CourseStepStatus.none);

        // 신청대기중인 과정정보가 없는 경우 리턴한다.
        if (courseAccount == null) return 3;

        // 교육정원 관련 신청할 수 있는지 체크(신청할 수 있는 경우 true 반환)
        if (!courseAccountService.isCourseRequestCapacity(courseId, userId)) {
            return 11;
        }

        return courseAccountService.accountVerification(userId);
    }

    /**
     * 교육안내>교육신청 사용자가 교육신청시 체크
     * @param
     * @return
     * @exception
     * @see
     */
    @PostMapping("/isVerification")
    public int accountVerification(@RequestParam("courseId") long courseId
            , @RequestParam String userId){

        // 과정 신청에 필요한 기본 검증을 진행한다.
        // 0: 계정이 존재하지 않음
        // 1: 상위결재권자가 지정되지 않음
        // 2: 관리자가 지정되지 않음
        // 9: 과정 신청 가능
        // 10: 과정신청기간이 아님
        // 11: 정원수가 초과됨

        Course course = courseService.getCourseById(courseId);

        // 과정 신청기간인지를 체크한다.
        // 외부교육인 경우는 신청기간 체크를 하지 않는다.(교육이수후 신청함으로)
        if (!course.getCourseMaster().getId().equals("BC0104")) {
            if (!DateUtil.isWithinRange(DateUtil.getTodayString(), course.getRequestFromDate(), course.getRequestToDate()))
                return 10;
        }

//        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserIdAndRequestType(courseId, userId, "1");

        CourseAccount courseAccount = courseAccountService.getByCourseIdAndUserId(courseId, userId);


        // 교육과정에 내가 지정되었거나 신청한 내역이 없으면
        if (courseAccount == null) {
            // 교육정원 관련 신청할 수 있는지 체크(신청할 수 있는 경우 true 반환)
            if (!courseAccountService.isCourseRequestCapacity(courseId, userId))
                return 11;
        } else {
            // 이미 신청정보가 있는 경우는 리턴한다.
            if (courseAccount.getRequestType().equals("1"))
                return 4;
        }

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
