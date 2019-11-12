package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="binder_cv_experience")
public class BinderCvExperience extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    private String name;

    @Column(length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private BinderCv binderCv;
}
