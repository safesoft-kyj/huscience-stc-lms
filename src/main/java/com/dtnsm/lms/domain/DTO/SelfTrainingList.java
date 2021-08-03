package com.dtnsm.lms.domain.DTO;

import com.dtnsm.lms.domain.constant.CourseStepStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SelfTrainingList {
    private String title;
    private String orgDepart;
    private String orgTeam;
    private String department;
    private String name;
    private String fromDate;
    private String toDate;
    private String period;
    private String completeDate;
    private String courseStatus;
    private String isCommit;
    private String commitStatus;
}
