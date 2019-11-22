package com.dtnsm.lms.mybatis.controller;

import com.dtnsm.lms.mybatis.dto.ReportForm1;
import com.dtnsm.lms.mybatis.dto.ReportForm2;
import com.dtnsm.lms.mybatis.service.ReportMapperService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    ReportMapperService reportMapperService;

    @PostMapping("/surveyChartData")
    public List<ReportForm2> r1(@RequestParam("courseId") Long courseId, @RequestParam("questionId") Long questionId) {

        List<ReportForm2> rpt = reportMapperService.getSurveyChart(courseId, questionId);

        return rpt;
    }

    @GetMapping("/surveyChartData")
    public List<ReportForm2> r2(@RequestParam("courseId") Long courseId, @RequestParam("questionId") Long questionId) {

        List<ReportForm2> rpt = reportMapperService.getSurveyChart(courseId, questionId);

        return rpt;
    }
}
