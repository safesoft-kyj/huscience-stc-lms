package com.dtnsm.lms.domain.datasource;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseTrainingLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class EmployeeTrainingLogSource {
    private List<CourseTrainingLog> courseTrainingLogs;
    private Account account;

    private String jobTitle;
    private String name;
    // 부서(부서 / 팀)
    private String depart;
    // 입사일
    private String inDate;
    private String toDate;
    private byte[] sign;


    public EmployeeTrainingLogSource(String name) {
        this.name = name;
    }

    public EmployeeTrainingLogSource(){}
}
