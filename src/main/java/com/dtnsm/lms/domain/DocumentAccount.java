package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.constant.ApprovalStatusType;
import com.dtnsm.lms.domain.constant.CourseRequestType;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="el_document_account")
@IdClass(DocumentAccountId.class)
public class DocumentAccount extends AuditorEntity<String> {

    @Id
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

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

    private Date apprDateTime1;

//    @Column(length = 30)
//    private String apprUserId1;

    @ManyToOne
    @JoinColumn(name = "appr_user_id1",columnDefinition="VARCHAR(30)")
    private Account apprUserId1;

    // 팀장 승인일자
    @Column(length = 10)
    private String apprDate2;

    private Date apprDateTime2;

//    @Column(length = 30)
//    private String apprUserId2;

    @ManyToOne
    @JoinColumn(name = "appr_user_id2",columnDefinition="VARCHAR(30)")
    private Account apprUserId2;

    // 최종상태 : 1:신청완료(팀장승인진행중), 2:팀장승인완료(관리자확인중), 3:승인완료
    @Column(length = 30)
    @ColumnDefault(value = "0")
    @Enumerated(EnumType.STRING)
    private ApprovalStatusType status;

    // 종결 여부(최종승인이나 기각시, 1:종결, 0:진행중)
    private String isCommit = "0";
}
