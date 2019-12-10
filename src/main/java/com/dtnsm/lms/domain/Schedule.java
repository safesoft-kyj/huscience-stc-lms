package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.ScheduleType;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_schedule")
public class Schedule extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // C: 교육연간일정, M:Employee training metrix
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private ScheduleType sctype;

    // 등록년도
    @Column(length = 4)
    private String year;

    //@NotEmpty(message = "No title")
    @Column(length = 100, nullable = false)
    private String title;

    //@NotEmpty(message = "No content")
    @Column(columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    private int viewCnt;

    // 1: 기본 일정
    @ColumnDefault("0")
    private int isActive = 0;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleFile> scheduleFiles = new ArrayList<>();

    public Schedule(){}

    public Schedule(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
