package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name="el_course_survey_action_answer")
public class CourseSurveyActionAnswer extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 사용자정답
    @Column(length = 500)
    private String userAnswer;

    // 구분(S:주관식, M:객관식
    @Column(length = 1)
    private String surveyGubun;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "survey_action_id")
    private CourseSurveyAction surveyAction;

    @OneToOne(fetch = FetchType.LAZY)
    private CourseSurveyQuestion question;

    public CourseSurveyActionAnswer(){}
}
