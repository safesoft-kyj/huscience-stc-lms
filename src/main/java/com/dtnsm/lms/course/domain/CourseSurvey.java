package com.dtnsm.lms.course.domain;

import com.dtnsm.lms.audit.AuditorCreateEntity;
import com.dtnsm.lms.base.ElMinor;
import lombok.Getter;
import lombok.Setter;

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
//
//    @Column(name="type", length = 10)
//    private String type;

    // 문제
    @Column(name="name")
    private String name;

    // 설문시작일
    @Column(name="from_date", length = 10)
    private String fromDate;

    // 설문종료일
    @Column(name="to_date", length = 10)
    private String toDate;

    // 참여일
    @Column(name="exam_date", length = 10)
    private String examDate;

    // 참여시간
    @Column(name="exam_time", length = 8)
    private int examTime;

    // 질문수
    @Column(name="question_count")
    private int questionCount = 0;

    // 예제수(예제수가 0이면 주관식 그외는 객관식)
    @Column(name="item_count")
    private int itemCount = 0;

    // 문제 유형(BC0201:객관식, BC0202:주관식) => Major Code : BC02
    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "type")
    private ElMinor type;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSurveyQuestion> questions = new ArrayList<>();
}
