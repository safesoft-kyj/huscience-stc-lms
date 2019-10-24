package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.mybatis.dto.CourseCountVO;
import com.dtnsm.lms.mybatis.dto.DepartVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper {

    CourseCountVO selectCourseCount();


}
