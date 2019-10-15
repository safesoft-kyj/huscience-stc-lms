package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_course_quiz")
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

    // 시험시작일
    @Column(name="from_date", length = 10)
    private String fromDate;

    // 시험종료일
    @Column(name="to_date", length = 10)
    private String toDate;

    // 시험응시일
    @Column(name="exam_date", length = 10)
    private String examDate;

    // 시험응시시간
    @Column(name="exam_time", length = 8)
    private int examTime;

    // 질문수
    @Column(name="question_count")
    private int questionCount = 0;

    // 예제수(예제수가 0이면 주관식 그외는 객관식)
    @Column(name="item_count")
    private int itemCount = 0;

    // 시험시간(분)
    @Column(name="examMinute")
    private int examMinute = 0;

    // 점수
    @Column(name="score")
    private int score;

    // 문제 유형(BC0201:객관식, BC0202:주관식) => Major Code : BC02
    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "type")
    private ElMinor type;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuizQuestion>  quizQuestions = new ArrayList<>();
}
