package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "el_cv_computer_certification")
@NoArgsConstructor
public class CVComputerCertification extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -2645128110703703763L;
    @Id
    @SequenceGenerator(name = "CV_COM_CERTIFICATION_SEQ_GENERATOR", sequenceName = "SEQ_CV_COM_CERTIFICATION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_COM_CERTIFICATION_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "com_id", referencedColumnName = "id")
    private CVComputerKnowledge computerKnowledge;

    @Column(name = "certificate_program", columnDefinition = "nvarchar(500)")
    private String certificateProgram;
}

