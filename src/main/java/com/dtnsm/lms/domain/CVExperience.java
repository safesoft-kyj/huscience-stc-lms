package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.converter.RoleConverter;
import com.dtnsm.lms.domain.constant.GlobalOrLocal;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("JpaDataSourceORMInspection")
@Data
@Entity
@Table(name = "el_cv_experience")
@NoArgsConstructor
public class CVExperience extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -5269508620489212051L;
    @Id
    @SequenceGenerator(name = "CV_EXPERIENCE_SEQ_GENERATOR", sequenceName = "SEQ_CV_EXPERIENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_EXPERIENCE_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @Column(name = "ta")
    private String ta;

    @Column(name = "ta_other", length = 500)
    private String taOther;

    @Column(name = "indication")
    private String indication;

    @Column(name = "indication_other", length = 500)
    private String indicationOther;

    @Column(name = "phase")
    private String phase;

    @Column(name = "phase_other", length = 500)
    private String phaseOther;

    @Column(name = "global_or_local")
    @Enumerated(EnumType.STRING)
    private GlobalOrLocal globalOrLocal;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role")
    private String[] role;

    @Column(name = "role_other")
    private String roleOther;

    @Column(name = "workingDetails", columnDefinition = "nvarchar(1000)")
    private String workingDetails;

    @Column(name = "readonly")
    private boolean readOnly;
}

