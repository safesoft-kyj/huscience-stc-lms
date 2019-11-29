package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class LanguageDTO {
    private String language;
    private String level;
    private List<String> certificateProgramList;

    @Builder
    public LanguageDTO(String language, String level, List<String> certificateProgramList) {
        this.language = language;
        this.level = level;
        this.certificateProgramList = certificateProgramList;
    }
}
