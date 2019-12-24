package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class EducationDTO {
    private String startDate;
    private String endDate;
    private String nameOfUniversity;
    private String cityCountry;
    private String bachelorsDegree;
    private String mastersDegree;
    private String phdDegree;

    private String mastersThesisTitle;
    private String phdThesisTitle;

    private String mastersName;
    private String phdName;

    @Builder
    public EducationDTO(String startDate, String endDate, String nameOfUniversity, String cityCountry,
                        String bachelorsDegree, String mastersDegree, String phdDegree, String mastersThesisTitle, String phdThesisTitle,
                        String mastersName, String phdName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.nameOfUniversity = nameOfUniversity;
        this.cityCountry = cityCountry;

        this.bachelorsDegree = bachelorsDegree;
        this.mastersDegree = mastersDegree;
        this.phdDegree = phdDegree;

        this.mastersThesisTitle = mastersThesisTitle;
        this.phdThesisTitle = phdThesisTitle;

        this.mastersName = mastersName;
        this.phdName = phdName;
    }

    public boolean isMasters() {
        return mastersDegree != null && !"".equals(mastersDegree);
    }

    public boolean isPhd() {
        return phdDegree != null && !"".equals(phdDegree);
    }
}
