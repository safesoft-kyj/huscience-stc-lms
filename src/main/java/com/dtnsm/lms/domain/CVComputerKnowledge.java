package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.SkillLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "el_cv_computer_knowledge")
@NoArgsConstructor
public class CVComputerKnowledge extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = -4409916705024970304L;
    @Id
    @SequenceGenerator(name = "CV_COM_KNOWLEDGE_SEQ_GENERATOR", sequenceName = "SEQ_CV_COM_KNOWLEDGE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_COM_KNOWLEDGE_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private SkillLevel level;

    @OneToMany(mappedBy = "computerKnowledge")
    private List<CVComputerCertification> computerCertifications = new ArrayList<>();


    @Transient
    private boolean readOnly;
}

