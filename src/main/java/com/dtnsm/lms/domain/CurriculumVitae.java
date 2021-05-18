package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "el_curriculum_vitae")
@NoArgsConstructor
@ToString(of = {"id"})
@Audited(withModifiedFlag = true)
public class CurriculumVitae extends AuditorEntity<String> implements Serializable, Cloneable {
    private static final long serialVersionUID = -6522318781199162543L;
    @Id
    @SequenceGenerator(name = "CV_SEQ_GENERATOR", sequenceName = "SEQ_CV", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_SEQ_GENERATOR")
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    @ManyToOne
    private Account account;

    @Column(name = "sign_date")
    private Date signDate;

    @Column(name = "initial_name")
    private String initialName;

    //최초 버전
    @Column(name = "initial")
    private boolean initial;

    @Column(name = "reviewer_id")
    private String reviewerId;

    @Column(name = "reviewed_date")
    private Date reviewedDate;

    @Column(name = "base64sign", columnDefinition = "varchar(max)")
    private String base64sign;

    @Column(name = "html_content", columnDefinition = "nvarchar(max)")
    private String htmlContent;

    @Column(name = "cv_file_name")
    private String cvFileName;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "reviewed")
    @ColumnDefault(value = "'false'")
    private boolean reviewed;

    @Transient
    private int pos;

    public double getProgress() {
        return (((double)pos + 1) / 6) * 100;
    }

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private CurriculumVitaeStatus status;

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVEducation> educations = new ArrayList<>();

    @Transient
    private List<CVEducation> removeEducations = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVCareerHistory> careerHistories = new ArrayList<>();

    @Transient
    private List<CVCareerHistory> removeCareerHistories = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVLicense> licenses = new ArrayList<>();

    @Transient
    private List<CVLicense> removeLicenses = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVCertification> certifications = new ArrayList<>();

    @Transient
    private List<CVCertification> removeCertifications = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVMembership> memberships = new ArrayList<>();

    @Transient
    private List<CVMembership> removeMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVLanguage> languages = new ArrayList<>();

    @Transient
    private List<CVLanguage> removeLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVComputerKnowledge> computerKnowledges = new ArrayList<>();

    @Transient
    private List<CVComputerKnowledge> removeComputerKnowledges = new ArrayList<>();

    @OneToMany(mappedBy = "curriculumVitae")
    private List<CVExperience> experiences = new ArrayList<>();

    @Transient
    private List<CVExperience> removeExperiences = new ArrayList<>();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
