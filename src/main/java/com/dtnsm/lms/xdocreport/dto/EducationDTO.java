package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class EducationDTO {
    private String startDate;
    private String endDate;
    private String nameOfUniversity;
    private String cityCountry;
    private String degree;
    private String thesisTitle;
    private String nameOfSupervisor;

    @Builder
    public EducationDTO(String startDate, String endDate, String nameOfUniversity, String cityCountry, String degree, String thesisTitle, String nameOfSupervisor) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.nameOfUniversity = nameOfUniversity;
        this.cityCountry = cityCountry;
        this.degree = degree;
        this.thesisTitle = thesisTitle;
        this.nameOfSupervisor = nameOfSupervisor;
    }
}
