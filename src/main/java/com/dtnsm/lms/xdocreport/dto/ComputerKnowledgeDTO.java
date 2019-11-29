package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ComputerKnowledgeDTO {
    private String name;
    private String level;
    private List<String> certificateProgramList;

    @Builder
    public ComputerKnowledgeDTO(String name, String level, List<String> certificateProgramList) {
        this.name = name;
        this.level = level;
        this.certificateProgramList = certificateProgramList;
    }
}
