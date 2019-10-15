package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="el_course_survey_question_item")
public class CourseSurveyQuestionItem extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 객관식 질문 항목
    @Column(name="item")
    private String item;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "question_id")
    private CourseSurveyQuestion question;
}
