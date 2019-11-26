package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.GlobalOrLocal;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

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

    @ManyToOne
    @JoinColumn(name = "indication_id", referencedColumnName = "id")
    private CVIndication indication;

    @ManyToOne
    @JoinColumn(name = "phase_id", referencedColumnName = "id")
    private CVPhase phase;

    @Column(name = "global_or_local")
    @Enumerated(EnumType.STRING)
    private GlobalOrLocal globalOrLocal;

    @Column(name = "role")
    private String role;

    @Column(name = "workingDetails")
    private String workingDetails;

    @Transient
    private boolean readOnly;
}

