package com.dtnsm.lms.course.domain;


import com.dtnsm.lms.audit.AuditorCreateEntity;
import lombok.Data;

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

    @OneToMany(mappedBy = "courseMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    @Column(length = 10, nullable = false)
    private String minorCd;

    @Column(length = 1, nullable = false)
    private String isMail = "N";

//
//    @Transient
//    private String minorName;

    public CourseMaster(){}

    public CourseMaster(String courseName) {
        this.courseName = courseName;
    }
}
