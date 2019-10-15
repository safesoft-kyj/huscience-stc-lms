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

    @Column(length = 10, nullable = false)
    private String minorCd;

//    @Column(length = 1, nullable = true)
//    @ColumnDefault("N")
//    private String isMail;

    // 교육신청유형(1:상시, 2:기간) =>Major code : BC04
    @Column(length = 1, nullable = false)
    @ColumnDefault("1")
    private String requestType = "1";

//
//    @Transient
//    private String minorName;

    public CourseMaster(){}

    public CourseMaster(String courseName) {
        this.courseName = courseName;
    }
}
