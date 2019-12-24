package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ExperienceDTO {
    private String ta;
    private String indication;
    private String phase;
    private String globalOrLocal;
    private List<String> roles;
    private String workingDetails;

    @Builder
    public ExperienceDTO(String ta, String indication, String phase, String globalOrLocal, List<String> roles, String workingDetails) {
        this.ta = ta;
        this.indication = indication;
        this.phase = phase;
        this.globalOrLocal = globalOrLocal;
        this.roles = roles;
        this.workingDetails = workingDetails;
    }
}
