package com.dtnsm.lms.domain;

import lombok.Data;

@Data
public class Project {

    private String name;
    private String certiNo;
    private String courseTitle;
    private String courseDate;
    private String firstUserName;
    private String secondUserName;
    private String sopName;
    private String effectiveDate;


    public Project(){}
    public Project(String name) {
        this.name = name;
    }
}