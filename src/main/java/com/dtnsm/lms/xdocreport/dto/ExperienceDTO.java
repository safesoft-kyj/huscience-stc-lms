package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ExperienceDTO {
    private String indication;
    private String phase;
    private String globalOrLocal;
    private String role;
    private String workingDetails;

    @Builder
    public ExperienceDTO(String indication, String phase, String globalOrLocal, String role, String workingDetails) {
        this.indication = indication;
        this.phase = phase;
        this.globalOrLocal = globalOrLocal;
        this.role = role;
        this.workingDetails = workingDetails;
    }
}
