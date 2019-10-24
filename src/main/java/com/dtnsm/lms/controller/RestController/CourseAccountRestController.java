package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.mybatis.dto.DepartTreeVO;
import com.dtnsm.lms.mybatis.service.DepartMapperService;
import com.dtnsm.lms.service.CourseAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
