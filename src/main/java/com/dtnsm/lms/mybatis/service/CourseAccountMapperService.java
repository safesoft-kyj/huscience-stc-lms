package com.dtnsm.lms.mybatis.service;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.mybatis.mapper.CourseAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseAccountMapperService {

    @Autowired
    private CourseAccountMapper courseAccountMapper;

    public List<CourseAccount> getCourseReportAlarm(String typeId, int day) {
        return courseAccountMapper.selectBeforeCourseReportAlarm(typeId, day);    }



    // self 교육일때 종료 7일전 알람 발송
    public List<CourseAccount> getCourseToDateAlarm(String typeId, int day) {
        return courseAccountMapper.selectBeforeCourseToDateAlarm(typeId, day);
    }
}
