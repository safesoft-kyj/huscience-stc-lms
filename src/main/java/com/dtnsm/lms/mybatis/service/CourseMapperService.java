package com.dtnsm.lms.mybatis.service;

import com.dtnsm.lms.mybatis.dto.CourseCountVO;
import com.dtnsm.lms.mybatis.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseMapperService {

    @Autowired
    private CourseMapper courseMapper;

    public CourseCountVO getCourseCount() {
        return courseMapper.selectCourseCount();
    }

}
