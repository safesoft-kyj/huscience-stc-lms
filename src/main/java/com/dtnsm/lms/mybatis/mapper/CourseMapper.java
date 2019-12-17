package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.mybatis.dto.CourseCalendarVO;
import com.dtnsm.lms.mybatis.dto.CourseCountVO;
import com.dtnsm.lms.mybatis.dto.DepartVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper {

    CourseCountVO selectCourseCount();

    List<CourseCalendarVO> selectCourseCalenda1(String start, String end);

    List<CourseCalendarVO> selectCourseCalenda2(String start, String end);

    List<CourseCalendarVO> selectUserCourseCalenda1(String start, String end, String userId);

    List<CourseCalendarVO> selectUserCourseCalenda2(String start, String end, String userId);
}
