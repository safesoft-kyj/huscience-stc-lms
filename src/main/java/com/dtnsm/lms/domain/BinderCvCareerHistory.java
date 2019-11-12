package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="binder_cv_career_history")
public class BinderCvCareerHistory extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    private String company;

    private String companyAddress;

    private String period;

    private String position;

    private String TeamDepartment;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private BinderCv binderCv;

}
