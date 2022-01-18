package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name="el_course_account")
//@IdClass(CourseAccountId.class)
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class CourseAccount extends AuditorEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 교육과정
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // 시작일
    @Column(length = 10)
    @ColumnDefault("'1900-01-01'")
    private String fromDate;

    // 종료일
    @Column(length = 10)
    @ColumnDefault("'1900-01-01'")
    private String toDate;

    // 기간연장 Count
    @ColumnDefault("0")
    private int periodExtendCount = 0;
//STC업뎃용
    // 교육 신청자
    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;
//stc적용
    @OneToMany(mappedBy = "courseAccount", cascade = CascadeType.ALL)
    private List<CourseTrainingLog> courseTrainingLogList;

    // 교육신청유형 0:관리자지정, 1:사용자 신청
    @Column(length = 1)
    private String requestType;

    @Column(length = 10)
    private String requestDate;

    // 신청일
    private Date fnWdate;

    // 교육과정 진행 상태
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private CourseStepStatus courseStatus = CourseStepStatus.none;

    @Column(nullable = false)
    @ColumnDefault("0")
    private boolean isTestFail;

    // 총결재자 수
    @ColumnDefault("0")
    private int fnFinalCount = 0;

    // 현재결재자 순번
    @ColumnDefault("0")
    private int fnCurrSeq = 0;

    // 전자결재 진행여부(팀장/부서장 결재가 Y이면) => 과정개설시 설정
    // 0: 진행안함, 1: 전자결재 진행
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isApproval = "0";

    // 팀장/부서장 승인 여부(Y, N)
    @ColumnDefault("''")
    private String isTeamMangerApproval;

    // 과정 관리자 승인 여부(Y, N)
    @ColumnDefault("''")
    private String isCourseMangerApproval;

    // 전자 결재 상태 : 0: 진행중, 1: 승인, 2:기각, 9:미진행
    @Column(length = 1)
    @ColumnDefault("'9'")
    private String fnStatus = "9";

    @Column(length = 30)
    private String certificateNo;

    // 수료증 발급일
    private Date certificateBindDate;

    /*
    교육과정 참석 유무(0:미참석, 1:참석완료)
        1. self training 은 과정신청시 바로 1로 변경 -> (시험,설문,수료증) 완료시 isCommit 1로 변경 -> Training log 생성

        3. 외부교육은 외부교육참석보고서 승인 완료시 1로 변경 -> isCommit 1로 변경 -> Trainig log 생성

        2. class training 교육 신청결재 완료후 관리자가 교육참석등록시 1로 변경 -> (시험,설문,수료증) 완료시 isCommit 1로 변경 -> Training log 생성
        4. 부서별교육은 교육참석등록시 1로 변경 -> isCommit 1로 변경 -> Training log 생성

     */
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isAttendance  = "0";

    // 교육과정 종결
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isCommit = "0";

    // 보고서 작성 유무(외부교육인 경우 작성해야 함)
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isReport = "0";

    // 외부교육은 보고서 작성이 있음으로 상태값을 관리한다. 0: 진행중, 1: 승인, 2:기각, 8:임시저장, 9:미진행
    @Column(length = 1)
    @ColumnDefault("'9'")
    private String reportStatus = "9";

//    @Column(nullable = true)
//    @ManyToOne
//    @JoinColumn(name = "document_id")
//    @ColumnDefault("0")
//    private long document_id=0;

    @OneToOne(mappedBy = "courseAccount")
    private CourseCertificateLog courseCertificateLog;

    @OneToMany(mappedBy = "courseAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "courseAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSectionAction> courseSectionActions = new ArrayList<>();

    @OneToMany(mappedBy = "courseAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseQuizAction> courseQuizActions = new ArrayList<>();

    @OneToMany(mappedBy = "courseAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSurveyAction> courseSurveyActions = new ArrayList<>();

    @OneToMany(mappedBy = "courseAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseAccountOrder> courseAccountOrders = new ArrayList<>();


    public boolean addCourseAccountOrder(CourseAccountOrder courseAccountOrder) {
        if(courseAccountOrders == null)
            courseAccountOrders = new ArrayList<>();

        return courseAccountOrders.add(courseAccountOrder);
    }
}
