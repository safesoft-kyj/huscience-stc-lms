package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="binder_cv_skills")
public class BinderCvSkills extends AuditorEntity<String> {

    @Id
    @GeneratedValue
    private long id;

    private String languages;

    private String languagesLevel;

    private String computerKnowledge;

    private String computerKnowledgeLevel;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private BinderCv binderCv;
}
