package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SurveyStatusType;
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
@Table(name="el_course_survey")
@Audited(withModifiedFlag = true)
public class CourseSurvey extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 문제
    @Column(name="name")
    private String name;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_id")
    @NotAudited
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
