package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_border")
public class Border extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    //@NotEmpty(message = "No title")
    @Column(length = 100, nullable = false)
    private String title;

    //@NotEmpty(message = "No content")
    @Column(columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    private int viewCnt;

    @ColumnDefault("0")
    private int replyCnt;

    @Column(length = 1)
    @ColumnDefault("0")
    private String isNotice = "0";  // 0:일반글, 1:공지글

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "type_id")
    private BorderMaster borderMaster;

    @OneToMany(mappedBy = "border", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorderFile> borderFiles = new ArrayList<>();

    public Border(){}

    public Border(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Border(String title, String content, BorderMaster borderMaster) {
        this.title = title;
        this.content = content;
        this.borderMaster = borderMaster;
    }
}
