package com.dtnsm.lms.domain.datasource;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseTrainingLog;
import lombok.Data;

import java.util.List;

@Data
public class CertificateSource {
    private String no;
    // 영문명
    private String name;

    private String title;

    private String sopName;

    private String sopEffDate;

    // 코스명
    private String courseTitle;

    // 부서(부서 / 팀)
    private String depart;

    // 교육일
    private String prior;

    private String manName1;
    private String manName2;

    private String depart1;
    private String depart2;

    private byte[] sign1;
    private byte[] sign2;

    public CertificateSource(String name) {
        this.name = name;
    }

    public CertificateSource(){}
}
