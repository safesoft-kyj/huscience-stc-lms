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
@Table(name = "el_cv_career_history")
@NoArgsConstructor
public class CVCareerHistory extends AuditorEntity<String> implements Serializable {

    private static final long serialVersionUID = -3998579250253963340L;
    @Id
    @SequenceGenerator(name = "CV_CAREER_HISTORY_SEQ_GENERATOR", sequenceName = "SEQ_CV_CAREER_HISTORY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_CAREER_HISTORY_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @Column(name = "companyName", length = 50)
    private String companyName;

    @Column(name = "city_country")
    private String cityCountry;

    @DateTimeFormat(pattern = "MMM yyyy")
    @Column(name = "start_date")
    private Date startDate;

    @DateTimeFormat(pattern = "MMM yyyy")
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "present")
    private boolean present;

    @Column(name = "position")
    private String position;

    @Column(name = "clinical_trial_experience")
    private boolean clinicalTrialExperience;

    @Column(name = "team_department")
    private String teamDepartment;

    @Transient
    private boolean readOnly;
}
