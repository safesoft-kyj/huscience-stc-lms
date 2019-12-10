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
@Table(name = "el_cv_membership")
@NoArgsConstructor
public class CVMembership extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = 7214340835824954397L;
    @Id
    @SequenceGenerator(name = "CV_MEMBERSHIP_SEQ_GENERATOR", sequenceName = "SEQ_CV_MEMBERSHIP", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_MEMBERSHIP_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @Column(name = "membership_name")
    private String membershipName;

    @Column(name = "start_year")
    private String startYear;

    @Column(name = "end_year")
    private String endYear;

    @Column(name = "present")
    private boolean present;

    @Column(name = "readonly")
    private boolean readOnly;
}
