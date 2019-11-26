package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "el_cv_phase", uniqueConstraints = @UniqueConstraint(columnNames = "phase"))
@NoArgsConstructor
public class CVPhase extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -5269508620489212051L;
    @Id
    @SequenceGenerator(name = "CV_PHASE_SEQ_GENERATOR", sequenceName = "SEQ_CV_PHASE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_PHASE_SEQ_GENERATOR")
    private Integer id;

    @Column(name = "phase")
    private String phase;
}

