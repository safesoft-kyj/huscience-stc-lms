package com.dtnsm.lms.domain.DTO;

import com.dtnsm.lms.domain.constant.CourseStepStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-08-11 오후 5:07
 * @desc Github : https://github.com/pub147
 **/
@Getter
@Setter
@Builder
public class CourseAccountSimple {
    private String id;
    private String orgDepart;
    private String name;
    private String engName;
    private String fromDate;
    private String toDate;
    private String requestDate;
    private String completeDate;
    private String requestType;
    private String fnStatus;
    private CourseStepStatus courseStatus;
    private String isCommit;
    private String isAlways;
    private String typeId;
    private String isAttendance;
    private String certificateBindDate;
}
