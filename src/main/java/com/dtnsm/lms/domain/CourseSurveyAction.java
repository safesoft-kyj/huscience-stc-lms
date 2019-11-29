package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_course_survey_action")
public class CourseSurveyAction extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 실행일
    @Column(name="exam_date", length = 10)
    private String executeDate;

    // 문항수
    @Column(name="question_count")
    private int questionCount;

    // 점수
    @Column(name="score")
    private int score;

    @Column(name="status", length=10)
    @ColumnDefault(value = "0")
    @Enumerated(EnumType.STRING)
    private SurveyStatusType status;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private CourseAccount courseAccount;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_survey_id")
    private CourseSurvey courseSurvey;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    @OneToMany(mappedBy = "surveyAction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSurveyActionAnswer> actionAnswers = new ArrayList<>();

    public boolean addQuizActionAnswer(CourseSurveyActionAnswer courseSurveyActionAnswer) {
        if(courseSurveyActionAnswer == null)
            actionAnswers = new ArrayList<>();

        return actionAnswers.add(courseSurveyActionAnswer);
    }

}
