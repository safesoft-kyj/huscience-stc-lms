package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "el_cv_indication", uniqueConstraints = @UniqueConstraint(columnNames = "indication"))
@NoArgsConstructor
public class CVIndication extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -5269508620489212051L;
    @Id
    @SequenceGenerator(name = "CV_INDICATION_SEQ_GENERATOR", sequenceName = "SEQ_CV_INDICATION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_INDICATION_SEQ_GENERATOR")
    private Integer id;

    @Column(name = "indication")
    private String indication;
}

