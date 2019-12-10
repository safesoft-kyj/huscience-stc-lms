package com.dtnsm.lms.domain;

import com.dtnsm.common.entity.TrainingRecord;
import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "el_training_record_review")
@NoArgsConstructor
public class TrainingRecordReview extends AuditorEntity<String> implements Serializable {
    private static final long serialVersionUID = 1995584156964319966L;

    @Id
    @SequenceGenerator(name = "TR_REVIEW_SEQ_GENERATOR", sequenceName = "SEQ_TR_REVIEW", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TR_REVIEW_SEQ_GENERATOR")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "tr_id", referencedColumnName = "id")
    private TrainingRecord trainingRecord;

    @ManyToOne
    @JoinColumn(name = "cv_id", referencedColumnName = "id")
    private CurriculumVitae curriculumVitae;

    @OneToMany(mappedBy = "trainingRecordReview")
    private List<TrainingRecordReviewJd> trainingRecordReviewJdList = new ArrayList<>();

    @Column(name = "request_date")
    private Date requestDate;

    @Column(name = "cv_date_of_review")
    private Date cvDateOfReview;

    @Column(name = "jd_date_of_review")
    private Date jdDateOfReview;

    @Column(name = "tlog_date_of_review")
    private Date tlogDateOfReview;

}
