package com.dtnsm.lms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class DocSource {
    private List<CourseTrainingLog> courseTrainingLogs;
    private Account account;
    private String name;

    public DocSource(String name) {
        this.name = name;
    }

    public DocSource(){}
}
