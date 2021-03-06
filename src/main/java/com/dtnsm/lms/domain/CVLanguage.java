package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.SkillLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "el_cv_language")
@NoArgsConstructor
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class CVLanguage extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -58763108466687008L;
    @Id
    @SequenceGenerator(name = "CV_LANGUAGE_SEQ_GENERATOR", sequenceName = "SEQ_CV_LANGUAGE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_LANGUAGE_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @Column(name = "language")
    private String language;

    @Column(name = "language_other", columnDefinition = "nvarchar(500)")
    private String languageOther;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private SkillLevel level;

    @OneToMany(mappedBy = "language", cascade = CascadeType.REMOVE)
    private List<CVLanguageCertification> languageCertifications = new ArrayList<>();

    @Transient
    private List<CVLanguageCertification> removeLanguageCertifications = new ArrayList<>();

    @Column(name = "readonly")
    private boolean readOnly;
}

