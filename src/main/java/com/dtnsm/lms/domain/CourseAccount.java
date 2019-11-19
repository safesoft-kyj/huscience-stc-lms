package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.CourseStepStatus;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="el_course_account")
//@IdClass(CourseAccountId.class)
public class CourseAccount extends AuditorEntity<String> {

    @Id
    @GeneratedValue
    private long id;

    // 교육과정
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // 교육 신청자
    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;

    // 교육신청유형 0:관리자지정, 1:사용자 신청
    @Column(length = 1)
    private String requestType;

    @Column(length = 10)
    private String requestDate;

    // 신청일
    private Date fWdate;

    // 교육과정 진행 상태
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private CourseStepStatus courseStatus = CourseStepStatus.none;

    // 총결재자 수
    private int fFinalCount = 0;

    // 현재결재자 순번
    private int fCurrSeq = 0;


    // 전자결재 진행여부(팀장/부서장 결재가 Y이면) => 과정개설시 설정
    // 0: 진행안함, 1: 전자결재 진행
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String IsApproval = "0";


    // 전자 결재 상태 : 0: 진행중, 1: 승인, 2:기각
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fStatus = "0";

    @OneToMany(mappedBy = "courseAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseAccountOrder> courseAccountOrders = new ArrayList<>();

    // 교육과정 종결
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isCommit = "0";


    public boolean addCourseAccountOrder(CourseAccountOrder courseAccountOrder) {
        if(courseAccountOrders == null)
            courseAccountOrders = new ArrayList<>();

        return courseAccountOrders.add(courseAccountOrder);
    }
}
