package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_course_quiz_action")
public class CourseQuizAction extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 시험응시일
    @Column(name="exam_date", length = 10)
    private String examDate;

    // 총시험시간(분)
    @Column(name="run_minute")
    @ColumnDefault("0")
    private int minute;

    // 총시험시간(초)
    @Column(name="run_second")
    @ColumnDefault("0")
    private int second;

    // 학습한 시간(초) => 진도율 계산시 필요
    @Column(name="total_use_second")
    @ColumnDefault("0")
    private int totalUseSecond;

    // 점수
    @Column(name="score")
    private int score;

    // 맞은 갯수
    @Column(name="run_count")
    private int runCount;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private CourseQuiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    @OneToMany(mappedBy = "quizAction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuizActionAnswer> actionAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "quizAction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuizActionHistory> actionHistories = new ArrayList<>();

    public boolean addQuizActionHistory(CourseQuizActionHistory courseQuizActionHistory) {
        if(courseQuizActionHistory == null)
            actionHistories = new ArrayList<>();

        return actionHistories.add(courseQuizActionHistory);
    }

    public boolean addQuizActionAnswer(CourseQuizActionAnswer courseQuizActionAnswer) {
        if(courseQuizActionAnswer == null)
            actionAnswers = new ArrayList<>();

        return actionAnswers.add(courseQuizActionAnswer);
    }

}
