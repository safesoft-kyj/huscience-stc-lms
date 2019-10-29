package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_document")
public class Document extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    //@NotEmpty(message = "No title")
    @Column(length = 100, nullable = false)
    private String title;

    // 교육 대상자
    @Column(length = 1000)
    private String mailSender;

    //@NotEmpty(message = "No content")
    @Column(columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    private int viewCnt;

    // 사용자계정 필드 추가
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    // 결재양식 필드 추가
    @ManyToOne
    @JoinColumn(name = "template_id")
    private DocumentTemplate template;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentFile> documentFiles = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentAccount> documentAccountList;

    public Document(){}

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Document(String title, String content, DocumentTemplate template) {
        this.title = title;
        this.content = content;
        this.template = template;
    }
}
