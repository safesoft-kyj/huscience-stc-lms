package com.dtnsm.lms.mybatis.service;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.mybatis.dto.CourseCalendarVO;
import com.dtnsm.lms.mybatis.dto.CourseCountVO;
import com.dtnsm.lms.mybatis.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMapperService {

    @Autowired
    private CourseMapper courseMapper;

    public CourseCountVO getCourseCount() {
        return courseMapper.selectCourseCount();
    }


    public List<CourseCalendarVO> getCourseCalenda1(String start, String end) {
        return courseMapper.selectCourseCalenda1(start, end);
    }

    public List<CourseCalendarVO> getCourseCalenda2(String start, String end) {
        return courseMapper.selectCourseCalenda2(start, end);
    }

    public List<CourseCalendarVO> getUserCourseCalenda1(String start, String end, String userId) {
        return courseMapper.selectUserCourseCalenda1(start, end, userId);
    }

    public List<CourseCalendarVO> getUserCourseCalenda2(String start, String end, String userId) {
        return courseMapper.selectUserCourseCalenda2(start, end, userId);
    }
}
