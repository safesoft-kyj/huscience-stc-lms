package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CareerHistoryDTO {
    private String companyName;
    private String cityCountry;
    private String startDate;
    private String endDate;
    private List<TeamDeptDTO> teamDeptDTOList;

    @Builder
    public CareerHistoryDTO(String companyName, String cityCountry, String startDate, String endDate, List<TeamDeptDTO> teamDeptDTOList) {
        this.companyName = companyName;
        this.cityCountry = cityCountry;
        this.startDate = startDate;
        this.endDate = endDate;
        this.teamDeptDTOList = teamDeptDTOList;
    }
}
