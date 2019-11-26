package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "el_curriculum_vitae")
@NoArgsConstructor
public class CurriculumVitae extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -6522318781199162543L;
    @Id
    @SequenceGenerator(name = "CV_SEQ_GENERATOR", sequenceName = "SEQ_CV", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    private Account account;

    @Column(name = "sign_date")
    private Date signDate;

    @Column(name = "initial_name")
    private String initialName;

    //최초 버전
    @Column(name = "initial")
    private boolean initial;

    @Column(name = "base64sign", columnDefinition = "varchar(max)")
    private String base64sign;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private CurriculumVitaeStatus status;

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVEducation> educations = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVCareerHistory> careerHistories = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVLicense> licenses = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVCertification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVMembership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVLanguage> languages = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVComputerKnowledge> computerKnowledges = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVExperience> experiences = new ArrayList<>();
}
