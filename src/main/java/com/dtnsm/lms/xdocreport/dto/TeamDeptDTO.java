package com.dtnsm.lms.xdocreport.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class TeamDeptDTO {
    private String position;
    private String team;
    private String department;

    @Builder
    public TeamDeptDTO(String position, String team, String department) {
        this.position = position;
        this.team = team;
        this.department = department;
    }
}
