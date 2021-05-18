package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_course_section")
@Audited(withModifiedFlag = true)
public class CourseSection extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 강의명
    @Column(name="name")
    private String name;

    @Column(name="description", length = 1000)
    private String description;

    // 강사명
    private String teacher ;

    // 학습일
    @Column(name="study_date", length = 10)
    @ColumnDefault("1900-01-01")
    private String studyDate;

    // 총학습시간(시)
    @Column(name="section_hour", columnDefinition="numeric(5,2)")
    @ColumnDefault("0")
    private float hour;

    // 총학습시간(분)
    @Column(name="section_minute")
    @ColumnDefault("0")
    private int minute;

    // 총학습시간(초)
    @Column(name="section_second")
    @ColumnDefault("0")
    private int second;

    // 강의파일 총장수
    @Column(name="image_size")
    @ColumnDefault("0")
    private int imageSize=0;

//    // 문제 유형(BC0201:객관식, BC0202:주관식) => Major Code : BC02
//    // Parent 필드 추가
//    @ManyToOne
//    @JoinColumn(name = "type")
//    private ElMinor type;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "courseSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSectionFile> sectionFiles = new ArrayList<>();

    @OneToMany(mappedBy = "courseSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSectionAction> courseSectionActions = new ArrayList<>();

    public CourseSection() {
    }

    public boolean addCourseSectionAction(CourseSectionAction sectionAction) {
        if(courseSectionActions == null)
            courseSectionActions = new ArrayList<>();

        return courseSectionActions.add(sectionAction);
    }
}
