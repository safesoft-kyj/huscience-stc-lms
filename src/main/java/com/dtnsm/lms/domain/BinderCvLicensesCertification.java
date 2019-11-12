package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="binder_cv_Licenses_Certification")
public class BinderCvLicensesCertification extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    @Column(length = 2000)
    private String licenses;

    @Column(length = 2000)
    private String certifications;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private BinderCv binderCv;

}
