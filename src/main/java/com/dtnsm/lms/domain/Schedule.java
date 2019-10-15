package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Data
@Table(name="el_schedule")
public class Schedule extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    // 1:Year, 2:month
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

    public Schedule(){}

    public Schedule(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
