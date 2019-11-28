package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CareerHistoryDTO {
    private String companyName;
    private String cityCountry;
    private String startDate;
    private String endDate;
    private String position;
    private String teamDepartment;

    @Builder
    public CareerHistoryDTO(String companyName, String cityCountry, String startDate, String endDate, String position, String teamDepartment) {
        this.companyName = companyName;
        this.cityCountry = cityCountry;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
        this.teamDepartment = teamDepartment;
    }
}
