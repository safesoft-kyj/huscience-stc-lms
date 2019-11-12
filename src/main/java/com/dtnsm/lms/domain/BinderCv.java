package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="binder_cv")
public class BinderCv extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    // 등록일
    @Column(length = 10, nullable = false)
    private String regDate;

    @Column(length = 100)
    private String signature;

    @Column(length = 100)
    private String signature2;

    private double ver = 1.0;

    private String isActive = "0";

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition="VARCHAR(30)")
    private Account account;

    @OneToMany(mappedBy = "binderCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinderCvEducation> binderCvEducations = new ArrayList<>();

    @OneToMany(mappedBy = "binderCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinderCvCareerHistory> binderCvCareerHistories = new ArrayList<>();

    @OneToMany(mappedBy = "binderCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinderCvExperience> binderCvExperiences = new ArrayList<>();

    @OneToMany(mappedBy = "binderCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinderCvLicensesCertification> binderCvLicensesCertifications = new ArrayList<>();

    @OneToMany(mappedBy = "binderCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinderCvSkills> binderCvSkills = new ArrayList<>();

    @OneToMany(mappedBy = "binderCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinderCvProfessionalAffiliations> binderCvProfessionalAffiliations = new ArrayList<>();


    public boolean addEducation(BinderCvEducation obj) {
        if(binderCvEducations == null)
            binderCvEducations = new ArrayList<>();

        return binderCvEducations.add(obj);
    }

    public boolean addCareerHistory(BinderCvCareerHistory obj) {
        if(binderCvCareerHistories == null)
            binderCvCareerHistories = new ArrayList<>();

        return binderCvCareerHistories.add(obj);
    }

    public boolean addExperience(BinderCvExperience obj) {
        if(binderCvExperiences == null)
            binderCvExperiences = new ArrayList<>();

        return binderCvExperiences.add(obj);
    }

    public boolean addLicensesCertification(BinderCvLicensesCertification obj) {
        if(binderCvLicensesCertifications == null)
            binderCvLicensesCertifications = new ArrayList<>();

        return binderCvLicensesCertifications.add(obj);
    }

    public boolean addSkills(BinderCvSkills obj) {
        if(binderCvSkills == null)
            binderCvSkills = new ArrayList<>();

        return binderCvSkills.add(obj);
    }

    public boolean addProfessionalAffiliations(BinderCvProfessionalAffiliations obj) {
        if(binderCvProfessionalAffiliations == null)
            binderCvProfessionalAffiliations = new ArrayList<>();

        return binderCvProfessionalAffiliations.add(obj);
    }
}
