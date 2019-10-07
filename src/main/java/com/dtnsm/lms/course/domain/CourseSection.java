package com.dtnsm.lms.course.domain;

import com.dtnsm.lms.audit.AuditorCreateEntity;
import com.dtnsm.lms.base.ElMinor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_course_section")
public class CourseSection extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 강의명
    @Column(name="name")
    private String name;

    // 학습시작일
    @Column(name="from_date", length = 10)
    private String fromDate;

    // 학습시작시간
    @Column(name="from_time", length = 8)
    private String fromTime;

    // 학습종료일
    @Column(name="to_date", length = 10)
    private String toDate;

    // 학습종료시간
    @Column(name="to_time", length = 8)
    private String toTime;

    // 학습시간
    @Column(name="section_Minute")
    private int minute;

    // 문제 유형(BC0201:객관식, BC0202:주관식) => Major Code : BC02
    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "type")
    private ElMinor type;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "courseSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSectionFile> sectionFiles = new ArrayList<>();

    public CourseSection() {
    }
}
