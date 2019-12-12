package com.dtnsm.lms.mybatis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseCalendarVO {
    private long id;

    // 과정명
    private String title;

    // 신청시작일
    private String requestFromDate;

    // 신청시작일
    private String requestToDate;

    // 교육시작일
    private String fromDate;

    // 교육종료일
    private String toDate;

    // 0:서비스전, 1: 신청대기, 2:교육신청, 3:교육대기, 4:교육중, 5:교육종료
    private int status = 0;

    // 0:교육서비스 대기, 1: 교육신청 접수 시작
    private int active = 0;

    // Parent 필드 추가
    private String typeId;
}
