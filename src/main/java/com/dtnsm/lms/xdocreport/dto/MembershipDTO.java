package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class MembershipDTO {
    private String name;
    private String startYear;
    private String endYear;

    @Builder
    public MembershipDTO(String name, String startYear, String endYear) {
        this.name = name;
        this.startYear = startYear;
        this.endYear = endYear;
    }
}
