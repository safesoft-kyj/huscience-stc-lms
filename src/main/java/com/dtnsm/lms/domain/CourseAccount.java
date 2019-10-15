package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

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

    // 1:관리자 지정 필수대상자, 2:신청자
    @Column(length = 1)
    @ColumnDefault(value = "1")
    private String requestType;

    // 팀장승인여부 : Y, N
    @Column(length = 1)
    private String isAppr1;

    // 관리자승인여부 : Y, N
    @Column(length = 1)
    private String isAppr2;

    // 팀장승인 : 0:초기, 1:미승인, 2:팀장승인, 3: 팀장확인
    @Column(length = 10)
    private String apprDate1;

    @Column(length = 30)
    private String apprUserId1;

    // 팀장 승인일자
    @Column(length = 10)
    private String apprDate2;

    @Column(length = 30)
    private String apprUserId2;

    // 최종상태 : 0:초기, 1:신청, 2:교육
    @Column(length = 1)
    @ColumnDefault(value = "0")
    private String status;

}
