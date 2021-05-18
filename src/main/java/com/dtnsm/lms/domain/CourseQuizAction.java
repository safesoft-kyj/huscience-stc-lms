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
@Table(name="el_course_quiz_action")
@Audited(withModifiedFlag = true)
public class CourseQuizAction extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 실행일
    @Column(name="exam_date", length = 10)
    private String executeDate;

    // 학습한 시간(초) => 진도율 계산시 필요
    @Column(name="total_use_second")
    @ColumnDefault("0")
    private int totalUseSecond;

    // 문항수
    @Column(name="question_count")
    private int questionCount;

    // 점수
    @Column(name="score")
    private int score;

    // 한개의 액션을 몇번 실행했는지
    @Column(name="run_count")
    private int runCount;

    // 시험을 여러번 친경우 최종 상태를 가진다 0:이전시험, 1:마지막 시험
    @ColumnDefault("'1'")
    private String isActive = "1";

    @Column(name="status", length=10)
    @ColumnDefault(value = "0")
    @Enumerated(EnumType.STRING)
    private QuizStatusType status;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private CourseAccount courseAccount;

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
