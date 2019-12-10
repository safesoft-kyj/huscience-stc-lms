package com.dtnsm.lms.domain;

import com.dtnsm.common.entity.JobDescriptionVersion;
import com.dtnsm.common.entity.TrainingRecord;
import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "el_training_record_review_jd")
@NoArgsConstructor
public class TrainingRecordReviewJd extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = 1995584156964319966L;

    @Id
    @SequenceGenerator(name = "TR_REVIEW_JD_SEQ_GENERATOR", sequenceName = "SEQ_TR_REVIEW_JD", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TR_REVIEW_JD_SEQ_GENERATOR")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private TrainingRecordReview trainingRecordReview;

    @ManyToOne
    @JoinColumn(name = "jd_ver_id", referencedColumnName = "id")
    private JobDescriptionVersion jobDescriptionVersion;
}
