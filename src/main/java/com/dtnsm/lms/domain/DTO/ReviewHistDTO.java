package com.dtnsm.lms.domain.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewHistDTO {
    private String displayName;
    private String jobTitle;
    private String dateStarted;
    private String deptTeam;
    private String employeeNo;

    private List<ReviewHist> reviewHistList = new ArrayList<>();
}
