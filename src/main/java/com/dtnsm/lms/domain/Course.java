package com.dtnsm.lms.domain;


import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name="el_course")
//@ToString
@Audited(withModifiedFlag = true)
public class Course extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 과정명
    //@NotEmpty(message = "No title")
    @Column(length = 255, nullable = false)
    private String title;

    // 과정소개
    //@NotEmpty(message = "No content")
    @Column(columnDefinition = "TEXT")
    private String content;

    // 신청시작일
    @Column(length = 10)
    @ColumnDefault("1900-01-01")
    private String requestFromDate;

    // 신청시작일
    @Column(length = 10)
    @ColumnDefault("1900-01-01")
    private String requestToDate;

    // 교육시작일
    @Column(length = 10)
    @ColumnDefault("'1900-01-01'")
    private String fromDate;

    // 교육종료일
    @Column(length = 10)
    @ColumnDefault("'1900-01-01'")
    private String toDate;

    // 교육장소
    @Column(length = 500)
    @ColumnDefault("''")
    private String place;

    // 교육일수
    @ColumnDefault("0")
    private int day;

    // 총학습시간(시)
    @Column(name="hour", columnDefinition="numeric(5,2)")
    @ColumnDefault("0")
    private float hour;

//    // 시험시간(시)
//    @Column(name="exam_hour", columnDefinition="numeric(5,2)")
//    @ColumnDefault("0")
//    private float examHour;

    // 교육정원
    @ColumnDefault("0")
    private int cnt ;

    // 수강료
    @ColumnDefault("0")
    private int amount ;

    // 참석대상 부서/팀
    @Column(length = 500)
    @ColumnDefault("''")
    private String team ;

    // 교제 제공 여부
    @ColumnDefault("'N'")
    @Column(length = 1)
    private String isBook = "N" ;

    // 시험 진행 여부
    @ColumnDefault("'N'")
    @Column(length = 1)
    private String isQuiz = "N" ;

    // 설문 진행 여부
    @ColumnDefault("'N'")
    @Column(length = 1)
    private String isSurvey = "N" ;

    // 수료증 발급 여부
    @ColumnDefault("'N'")
    @Column(length = 1)
    private String isCerti = "N" ;

    @Column(length = 20)
    private String certiHead;

    @Column(length = 1)
    @ColumnDefault("0")
    private String isNewEmpCourse = "0";  // 0:일반 Self 교육, 1:신입사원필수 Self 교육

    // 상시 교육 여부
    @Column(length = 1)
    @ColumnDefault("0")
    private String isAlways = "0" ;         // 0:기간, 1:상시

//   // 교육 대상자
//    @Column(length = 1000)
//    private String mailSender;

    // 뷰 카운터
    @ColumnDefault("0")
    private int viewCnt;



    // 0:서비스전, 1: 신청대기, 2:교육신청, 3:교육대기, 4:교육중, 5:교육종료
    @ColumnDefault("0")
    private int status = 0;

    // 0:교육서비스 대기, 1: 교육신청 접수 시작
    @ColumnDefault("0")
    private int active = 0;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "type_id")
    private CourseMaster courseMaster;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "el_course_account",
//            joinColumns = @JoinColumn(
//                    name = "course_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(
//                    name = "user_id", referencedColumnName = "userId"))
//    private Collection<Account> accounts;
//

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseAccount> courseAccountList;

//    // 과정별 사용자
//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CourseAccount> courseAccounts = new ArrayList<>();

    // 과정별 첨부파일
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseFile> courseFiles = new ArrayList<>();

    // 과정별 강의
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSection> sections = new ArrayList<>();

    // 과정별 시험
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuiz> quizzes = new ArrayList<>();

    // 과정별 설문
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

    public boolean addCourseAccount(CourseAccount courseAccount) {
        if(courseAccountList == null)
            courseAccountList = new ArrayList<>();

        if (courseAccountList.contains(courseAccount)) return false;

        return courseAccountList.add(courseAccount);
    }

    public void removeAllCourseAccount(List<CourseAccount> courseAccountList) {
        courseAccountList.removeAll(courseAccountList);
    }
}
