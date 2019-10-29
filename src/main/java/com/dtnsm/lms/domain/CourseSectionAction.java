package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.QuizStatusType;
import com.dtnsm.lms.domain.constant.SectionStatusType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="el_course_section_action")
public class CourseSectionAction extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    // 시험응시일
    @Column(name="exam_date", length = 10)
    private String examDate;

    // 총시험시간(초) => 진도율 계산시 필요
    @Column(name="total_use_second")
    @ColumnDefault("0")
    private int totalUseSecond;

    // 점수
    @Column(name="score")
    private int score;

    // 맞은 갯수
    @Column(name="run_count")
    private int runCount;

    @Column(name="status", length=10)
    @ColumnDefault(value = "0")
    @Enumerated(EnumType.STRING)
    private SectionStatusType status;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "section_id")
    private CourseSection courseSection;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    @OneToMany(mappedBy = "sectionAction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSectionActionHistory> courseSectionHistories = new ArrayList<>();

    public boolean addCourseSectionHistory(CourseSectionActionHistory sectionHistory) {
        if(courseSectionHistories == null)
            courseSectionHistories = new ArrayList<>();

        return courseSectionHistories.add(sectionHistory);
    }

}
