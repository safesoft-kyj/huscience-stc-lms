package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="el_course_quiz_action_history")
public class CourseQuizActionHistory extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @ColumnDefault("0")
    private int runCount;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;

    @Column(name="use_second")
    private int useSecond;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "quiz_action_id")
    private CourseQuizAction quizAction;
}
