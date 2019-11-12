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
@Table(name="el_course_survey")
public class CourseSurvey extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 문제
    @Column(name="name")
    private String name;

    // 설문시작일
    @Column(name="from_date", length = 10)
    private String fromDate;

    // 설문종료일
    @Column(name="to_date", length = 10)
    private String toDate;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToMany(mappedBy = "courseSurvey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSurveyQuestion> questions = new ArrayList<>();

    @OneToMany(mappedBy = "courseSurvey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSurveyAction> surveyActions = new ArrayList<>();
}
