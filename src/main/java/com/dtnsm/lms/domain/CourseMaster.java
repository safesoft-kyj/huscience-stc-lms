package com.dtnsm.lms.domain;


import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_course_master")
public class CourseMaster extends AuditorCreateEntity<String> {
    @Id
    @Column(length = 10, nullable = false)
    private String id;

    //@NotEmpty(message = "No title")
    @Column(length = 100, nullable = false)
    private String courseName;

    @OneToMany(mappedBy = "courseMaster", orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    //
    @ManyToOne
    @JoinColumn(name = "minor_cd")
    private ElMinor courseGubun;

//    @Column(length = 1, nullable = true)
//    @ColumnDefault("N")
//    private String isMail;

//    // 교육신청유형(1:상시, 2:기간) =>Major code : BC04
//    @Column(length = 1, nullable = false)
//    @ColumnDefault("1")
//    private String requestType = "1";

    // 팀장/부서장 승인 여부
    @ColumnDefault("'N'")
    private String isTeamMangerApproval = "N" ;

    // 과정 관리자 승인 여부
    @ColumnDefault("'N'")
    private String isCourseMangerApproval = "N" ;

    // 기본교육일수
    @ColumnDefault("0")
    private int day = 0;

    // 기본교육시간
    @Column(name="hour", columnDefinition="numeric(5,2)")
    @ColumnDefault("0")
    private float hour = 0;

//
//    @Transient
//    private String minorName;

    public CourseMaster(){}

    public CourseMaster(String courseName) {
        this.courseName = courseName;
    }
}
