package com.dtnsm.lms.mybatis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseCountVO {
    // self training count
    private String col01;
    // class training count
    private String col02;
    // 부서별 교육 count
    private String col03;
    // 외부 교육 count
    private String col04;
    // 과정 Total count
    private String totalCount;
}
