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
@Table(name = "el_cv_language_certification")
@NoArgsConstructor
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class CVLanguageCertification extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -2645128110703703763L;
    @Id
    @SequenceGenerator(name = "CV_LANG_CERTIFICATION_SEQ_GENERATOR", sequenceName = "SEQ_CV_LANG_CERTIFICATION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_LANG_CERTIFICATION_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lang_id", referencedColumnName = "id")
    private CVLanguage language;

    @Column(name = "certificate_program")
    private String certificateProgram;

    @Column(name = "certificate_program_other", columnDefinition = "nvarchar(500)")
    private String certificateProgramOther;

    @Column(name = "level_or_score")
    private String levelOrScore;
}

