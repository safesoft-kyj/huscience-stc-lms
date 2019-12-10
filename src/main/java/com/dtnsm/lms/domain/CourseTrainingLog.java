package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import com.dtnsm.lms.domain.constant.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="el_course_training_log")
public class CourseTrainingLog extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(length = 1000, nullable = false)
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
    private String isUpload = "0";

    // Section Action Id
    @ManyToOne
    @JoinColumn(name = "sec_act_id")
    private CourseSectionAction courseSectionAction;

    //
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;
}
