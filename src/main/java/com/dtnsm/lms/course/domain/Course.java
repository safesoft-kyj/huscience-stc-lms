package com.dtnsm.lms.course.domain;


import com.dtnsm.lms.audit.AuditorCreateEntity;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_course")
@ToString
public class Course extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    // 과정명
    //@NotEmpty(message = "No title")
    @Column(length = 100, nullable = false)
    private String title;

    // 과정소개
    //@NotEmpty(message = "No content")
    @Column(columnDefinition = "TEXT")
    private String content;

    // 교육 Agenda
    @Column(columnDefinition = "TEXT")
    private String agenda;

    // 신청시작일
    @Column(length = 10)
    private String requestFromDate;

    // 신청시작일
    @Column(length = 10)
    private String requestToDate;

    // 교육시작일
    @Column(length = 10)
    private String fromDate;

    // 교육종료일
    @Column(length = 10)
    private String toDate;

    // 교육장소
    @Column(length = 50)
    private String place;

    // 교육일수
    @ColumnDefault("0")
    private int day;

    // 교육시간
    @ColumnDefault("0")
    private int hour;

    // 교육정원
    @ColumnDefault("0")
    private int cnt ;

    // 수강료
    @ColumnDefault("0")
    private int amount ;

    // 강사명
    private String teacher ;

    // 참석대상 부서/팀
    @Column(length = 30)
    private String team ;

    // 교제 제공 여부
    private String isBook = "N" ;

    // 시험 진행 여부
    private String isQuiz = "N" ;

    // 설문 진행 여부
    private String isSurvey = "N" ;

    // 수료증 발급 여부
    private String isCerti = "N" ;

    // 교육 대상자
    @Column(length = 1000)
    private String mailSender;

    // 뷰 카운터
    @ColumnDefault("0")
    private int viewCnt;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "type_id")
    private CourseMaster courseMaster;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseFile> courseFiles = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuiz> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSurvey> surveys = new ArrayList<>();

    public Course(){}

    public Course(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Course(String title, String content, CourseMaster courseMaster) {
        this.title = title;
        this.content = content;
        this.courseMaster = courseMaster;
    }
}
