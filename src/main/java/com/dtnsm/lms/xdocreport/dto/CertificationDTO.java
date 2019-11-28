package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CertificationDTO {
    private String nameOfCertification;
    private String organizers;
    private String issueDate;

    @Builder
    public CertificationDTO(String nameOfCertification, String organizers, String issueDate) {
        this.nameOfCertification = nameOfCertification;
        this.organizers = organizers;
        this.issueDate = issueDate;
    }
}
