package com.dtnsm.lms.util;

import com.dtnsm.lms.service.CourseAccountService;
import org.springframework.stereotype.Component;

@Component
public class GlobalUtil {


    public static int apprProcessCount() {
        CourseAccountService courseAccountService = new CourseAccountService();
        return courseAccountService.getCourseAccountByUserId("pub147").size();
    }
}
