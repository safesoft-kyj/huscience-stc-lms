package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.domain.CourseAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseAccountMapper {

    // 외부교육 ToDate 익일 새벽에 외부교육참석보고서 작성 Alarm 발송 대상자 조회
    List<CourseAccount> selectBeforeCourseReportAlarm(String typeId, String day);


    // 교육기간이 임박한 과정 사용자 Alarm 발송 대상자 조회
    List<CourseAccount> selectBeforeCourseToDateAlarm(String typeId, String day);
}
