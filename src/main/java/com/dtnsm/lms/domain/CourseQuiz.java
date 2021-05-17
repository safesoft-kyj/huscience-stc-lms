package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_course_quiz")
@Audited(withModifiedFlag = true)
public class CourseQuiz extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

//    // 문제 유형(BC0201:객관식, BC0202:주관식) => Major Code : BC02
//    @Column(name="type", length = 10)
//    private String type;

    // 문제
    @Column(name="name")
    private String name;

    // 총학습시간(시)
    @Column(name="exam_hour", columnDefinition="numeric(5,2)")
    @ColumnDefault("0")
    private float hour;

    // 총시험시간(분)
    @Column(name="exam_minute")
    @ColumnDefault("0")
    private int minute;

    // 총시험시간(초)
    @Column(name="exam_second")
    @ColumnDefault("0")
    private int second;

    // 합격 문제 수
    @Column(name="path_count")
    private int passCount;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_id")
    @NotAudited
    private Course course;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuizFile> quizFiles = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuizQuestion>  quizQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuizAction> quizActions = new ArrayList<>();
}
