package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "el_cv_certification")
@NoArgsConstructor
public class CVCertification extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -6170291703919688555L;
    @Id
    @SequenceGenerator(name = "CV_CERTIFICATION_SEQ_GENERATOR", sequenceName = "SEQ_CV_CERTIFICATION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_CERTIFICATION_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @Column(name = "name_of_certification")
    private String nameOfCertification;

    @Column(name = "organizers")
    private String organizers;

    @DateTimeFormat(pattern = "MMM yyyy")
    @Column(name = "issue_date")
    private Date issueDate;

    @Transient
    private boolean readOnly;
}

