package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.TrainingType;
import com.dtnsm.lms.util.DateUtil;
import lombok.*;
import org.apache.poi.hpsf.Decimal;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Table(name="el_course_training_log")
@Audited(withModifiedFlag = true)
public class CourseTrainingLog extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "nvarchar(1000)")
    private String trainingCourse;

    // Organization type (SELF, OTHER)
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private TrainingType type;

    // Completion Date
    //    @Column(length = 11, nullable = false)
    private Date completeDate;

    @Column(name="training_time")
    private int trainingTime;

    @Column(name="organization", length = 50)
    private String organization;

    // 초기자료 업로드인지 시스템 발생자료인지 구분(0:초기 업로드 자료, 1:시스템 발생로그)
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isUpload;
//    private String isUpload = "0";

    // Section Action Id
    @ManyToOne
    @JoinColumn(name = "sec_act_id")
    private CourseSectionAction courseSectionAction;

    //
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "doc_no")
    private CourseAccount courseAccount;

    // 디지털 바이터 서식으로 리턴
    public String getCompleteDateFormat() {
        return DateUtil.getDateToString(this.completeDate, "dd-MMM-yyyy");
    }

    public String getTrainingTimeFormat() {
        DecimalFormat formatter = new DecimalFormat("##0.0#");
        return formatter.format(trainingTime * 1.0/3600);
    }
}
