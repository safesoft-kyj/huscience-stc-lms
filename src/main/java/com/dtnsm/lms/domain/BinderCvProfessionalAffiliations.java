package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="binder_cv_professional_affiliations")
public class BinderCvProfessionalAffiliations extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    private String membership;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private BinderCv binderCv;
}
