package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "el_cv_license")
@NoArgsConstructor
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class CVLicense extends AuditorEntity<String> implements Serializable {

    private static final long serialVersionUID = 1339989915897851851L;
    @Id
    @SequenceGenerator(name = "CV_LICENSE_SEQ_GENERATOR", sequenceName = "SEQ_CV_LICENSE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_LICENSE_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @Column(name = "name_of_license", columnDefinition = "nvarchar(500)")
    private String nameOfLicense;

    @Column(name = "license_no")
    private String licenseNo;

    @Column(name = "license_in_country")
    private String licenseInCountry;

    @Column(name = "license_in_country_other", columnDefinition = "nvarchar(100)")
    private String licenseInCountryOther;

    @Column(name = "readonly")
    private boolean readOnly;
}

