package com.dtnsm.lms.course.domain;

import com.dtnsm.lms.audit.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name="el_course_survey_question")
public class CourseSurveyQuestion extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 객관식 질문 항목
    @Column(name="question")
    private String question;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "survey_id")
    private CourseSurvey survey;

    // Parent 필드 추가
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSurveyQuestionItem> items = new ArrayList<>();
}
