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

    // 외부교육 ToDate 익일 새벽에 외부교육참석보고서 작성 Alarm 발송 3일전 대상자 조회
    public List<CourseAccount> getCourseReportAlarm3(String day) {
        return courseAccountMapper.selectBeforeCourseReportAlarm3(day);
    }

    // 외부교육 ToDate 익일 새벽에 외부교육참석보고서 작성 Alarm 발송 2일전 대상자 조회
    public List<CourseAccount> getCourseReportAlarm2(String day) {
        return courseAccountMapper.selectBeforeCourseReportAlarm2(day);
    }

    // 외부교육 ToDate 익일 새벽에 외부교육참석보고서 작성 Alarm 발송 1일전 대상자 조회
    public List<CourseAccount> getCourseReportAlarm1(String day) {
        return courseAccountMapper.selectBeforeCourseReportAlarm1(day);
    }

    // self 교육일때 종료 7일전 알람 발송
    public List<CourseAccount> getCourseToDateAlarm(String typeId, String day) {
        return courseAccountMapper.selectBeforeCourseToDateAlarm(typeId, day);
    }

    // 교육 수강 기간 만료 되었으나 교육을 완료하지 않은 교육대상자에게 보내지는 이메일
    public List<CourseAccount> getSelfTrainingExpirationToDateAlarm(Integer day) {
        return courseAccountMapper.getSelfTrainingExpirationToDateAlarm(day);
    }
}
