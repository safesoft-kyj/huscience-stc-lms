package com.dtnsm.lms.xdocreport.dto;

import com.dtnsm.lms.domain.constant.DegreeType;
import lombok.Builder;
import lombok.Data;

@Data
public class EducationDTO {
    private String startDate;
    private String endDate;
    private String nameOfUniversity;
    private String cityCountry;
    private DegreeType degreeType;
    private String degree;

    private String thesisTitle;

    private String nameOfSupervisor;

    public boolean isThesisTitle() {
        return degreeType != DegreeType.BACHELORS;
    }

    @Builder
    public EducationDTO(String startDate, String endDate, String nameOfUniversity, String cityCountry,
                        DegreeType degreeType,
                        String degree, String thesisTitle,
                        String nameOfSupervisor) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.nameOfUniversity = nameOfUniversity;
        this.cityCountry = cityCountry;

        this.degreeType = degreeType;
        this.degree = degree;
        this.thesisTitle = thesisTitle;
        this.nameOfSupervisor = nameOfSupervisor;
    }
}
