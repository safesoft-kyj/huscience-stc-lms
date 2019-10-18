package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.net.Authenticator;

@Entity
@Data
@Table(name="el_course_account")
@IdClass(CourseAccountId.class)
public class CourseAccount extends AuditorCreateEntity<String> {

    @Id
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Id
    @ManyToOne
//    @MapsId(value = "userId")
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;

    @Column(length = 10)
    private String requestDate;

    // 교육신청유형 : 1:관리자 지정, 2:팀장승인완료(관리자확인중), 3:승인완료
    @Column(length = 10)
    @ColumnDefault(value = "0")
    @Enumerated(EnumType.STRING)
    private CourseRequestType requestType;

    // 팀장승인여부 : Y, N
    @Column(length = 1)
    private String isAppr1="N";

    // 관리자승인여부 : Y, N
    @Column(length = 1)
    private String isAppr2="N";

    // 팀장/부서장 승인 여부
    @Column(length = 1)
    private String isTeamMangerApproval = "N" ;

    // 과정 관리자 승인 여부
    @Column(length = 1)
    private String isCourseMangerApproval = "N" ;

    // 팀장승인 : 0:초기, 1:미승인, 2:팀장승인, 3: 관리자확인, 4:
    @Column(length = 10)
    private String apprDate1;

    @Column(length = 30)
    private String apprUserId1;

    // 팀장 승인일자
    @Column(length = 10)
    private String apprDate2;

    @Column(length = 30)
    private String apprUserId2;

    // 최종상태 : 1:신청완료(팀장승인진행중), 2:팀장승인완료(관리자확인중), 3:승인완료
    @Column(length = 20)
    @ColumnDefault(value = "0")
    @Enumerated(EnumType.STRING)
    private ApprovalStatusType status;


}
