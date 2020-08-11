package com.dtnsm.lms.domain.DTO;

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
public class CourseSimple {
    private String id;
    private String title;

    private String requestFromDate;
    private String requestToDate;
    private String requestDatePrior;
    private String fromDate;
    private String toDate;
    private String datePrior;
    private Integer accountCnt;
    private Integer sectionCnt;
    private Integer quizCnt;
    private Integer surveyCnt;
    private String typeId;
    private String typeName;
    private String alwaysName;
    private String courseType;
    private Integer status;
    private Integer active;
    private String isAlways;
    private String isCerti;
    private String isQuiz;
    private String isSurvey;
}
