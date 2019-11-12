package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="binder_cv_education")
public class BinderCvEducation extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    private String eduPeriod;

     private String institute;

     private String instituteAddress;

     private String degree;

     private String thesisTitle;

     private String thesisOption;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private BinderCv binderCv;
}
