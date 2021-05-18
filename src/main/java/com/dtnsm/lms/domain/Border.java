package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_border")
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class Border extends AuditorEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    //@NotEmpty(message = "No title")
    @Column(length = 500, nullable = false)
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

    @Column(length = 10)
    private String fromDate;

    @Column(length = 10)
    private String toDate;

    // Parent 필드 추가
    @ManyToOne
    @JoinColumn(name = "type_id")
    private BorderMaster borderMaster;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;

    @OneToMany(mappedBy = "border", cascade = CascadeType.ALL, orphanRemoval = true)
    @AuditMappedBy(mappedBy = "border")
    private List<BorderViewAccount> borderViewAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "border", cascade = CascadeType.ALL, orphanRemoval = true)
    @AuditMappedBy(mappedBy = "border")
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
