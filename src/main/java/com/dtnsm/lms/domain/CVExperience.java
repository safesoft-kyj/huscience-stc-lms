package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.converter.RoleConverter;
import com.dtnsm.lms.domain.constant.GlobalOrLocal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("JpaDataSourceORMInspection")
@Data
@Entity
@Table(name = "el_cv_experience")
@NoArgsConstructor
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
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

    @Column(name = "ta_other", columnDefinition = "nvarchar(255)")
    private String taOther;

    @Column(name = "indication")
    private String indication;

    @Column(name = "indication_other", columnDefinition = "nvarchar(255)")
    private String indicationOther;

    @Column(name = "phase")
    private String phase;

    @Column(name = "phase_other", columnDefinition = "nvarchar(255)")
    private String phaseOther;

    @Column(name = "global_or_local")
    @Enumerated(EnumType.STRING)
    private GlobalOrLocal globalOrLocal;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role")
    private String[] role;

    @Column(name = "role_other", columnDefinition = "nvarchar(255)")
    private String roleOther;

    @Column(name = "workingDetails", columnDefinition = "nvarchar(4000)")
    private String workingDetails;

    @Column(name = "readonly")
    private boolean readOnly;
}

