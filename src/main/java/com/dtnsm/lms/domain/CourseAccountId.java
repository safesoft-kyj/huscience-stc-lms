package com.dtnsm.lms.domain;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class CourseAccountId implements Serializable {
    private static final long serialVersionUID = 1L;


//    @Column(name="course_id")
    private long course;

//    @Column(name="user_id")
    private String account;

    public CourseAccountId(){}
    public CourseAccountId(long course, String account){
        this.course = course;
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof CourseAccountId) && course == ((CourseAccountId)o).getCourse() && account == ((CourseAccountId) o).getAccount());
    }

    @Override
    public int hashCode() {

        return (int)(course);

    }

}
