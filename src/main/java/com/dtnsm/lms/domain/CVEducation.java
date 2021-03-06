package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.DegreeType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "el_cv_education")
@NoArgsConstructor
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class CVEducation extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = 1995584156964319966L;

    @Id
    @SequenceGenerator(name = "CV_EDUCATION_SEQ_GENERATOR", sequenceName = "SEQ_CV_EDUCATION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_EDUCATION_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @DateTimeFormat(pattern = "MMM yyyy")
    @Column(name = "start_date")
    private Date startDate;

    @DateTimeFormat(pattern = "MMM yyyy")
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "present")
    private boolean present;

    @Column(name = "name_of_university")
    private String nameOfUniversity;

    @Column(name = "name_of_university_other", columnDefinition = "nvarchar(255)")
    private String nameOfUniversityOther;

    @Column(name = "city_country")
    private String cityCountry;

    @Column(name = "city_country_other", columnDefinition = "nvarchar(255)")
    private String cityCountryOther;

    @Column(name = "degree_type")
    @Enumerated(EnumType.STRING)
    private DegreeType degreeType;

    @Column(name = "degree", columnDefinition = "nvarchar(500)")
    private String degree;

    @Column(name = "thesis_title", columnDefinition = "nvarchar(500)")
    private String thesisTitle;

    @Column(name = "name_of_supervisor", columnDefinition = "nvarchar(50)")
    private String nameOfSupervisor;

    @Column(name = "readonly")
    private boolean readOnly;
}
