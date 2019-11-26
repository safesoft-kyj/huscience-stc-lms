package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.mybatis.dto.*;

import java.util.List;

public interface ReportMapper {

    List<ReportForm1> selectSurveyReport(Long courseId);

    List<ReportForm3> selectSurveyReport2(Long courseId);

    List<ReportForm2> selectSurveyChart(Long courseId, Long questionId);

    List<QuizReportForm1> selectQuizReport(Long courseId, Long questionId);
}
