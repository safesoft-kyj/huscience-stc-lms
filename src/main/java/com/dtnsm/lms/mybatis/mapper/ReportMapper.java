package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.mybatis.dto.CourseCountVO;
import com.dtnsm.lms.mybatis.dto.ReportForm1;
import com.dtnsm.lms.mybatis.dto.ReportForm2;

import java.util.List;

public interface ReportMapper {

    List<ReportForm1> selectSurveyReport(Long courseId);

    List<ReportForm2> selectSurveyChart(Long courseId, Long questionId);
}
