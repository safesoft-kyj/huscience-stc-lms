package com.dtnsm.lms.mybatis.service;

import com.dtnsm.lms.mybatis.dto.*;
import com.dtnsm.lms.mybatis.mapper.CourseMapper;
import com.dtnsm.lms.mybatis.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportMapperService {

    @Autowired
    private ReportMapper reportMapper;

    public List<ReportForm1> getSurveyReport(Long courseId) {
        return reportMapper.selectSurveyReport(courseId);
    }

    public List<ReportForm3> getSurveyReport2(Long courseId) {
        return reportMapper.selectSurveyReport2(courseId);
    }

    public List<ReportForm2> getSurveyChart(Long courseId, Long questionId) {
        return reportMapper.selectSurveyChart(courseId, questionId);
    }

    public List<QuizReportForm1> getQuizReport(Long courseId, Long questionId) {
        return reportMapper.selectQuizReport(courseId, questionId);
    }
}
