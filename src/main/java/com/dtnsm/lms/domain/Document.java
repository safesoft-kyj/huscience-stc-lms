package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="el_document")
public class Document extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isCerti = "0";

    @ColumnDefault("0")
    private int viewCnt;

    @ManyToOne
    @JoinColumn(name = "course_doc_id",nullable = true)
    private CourseAccount courseAccount;

    // 사용자계정 필드 추가
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;
    @Column(length = 10)
    private String requestDate;

    // 신청일
    private Date fWdate;

    // 총결재자 수
    @ColumnDefault("0")
    private int fFinalCount = 0;

    // 현재결재자 순번
    @ColumnDefault("0")
    private int fCurrSeq = 0;

    // 전자 결재 상태 : 0: 진행중, 1: 승인, 2:기각
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fStatus = "0";

    // 최종 종결 유무
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isCommit = "0";


    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentAccountOrder> documentAccountOrders = new ArrayList<>();

    public boolean addCourseAccountOrder(DocumentAccountOrder documentAccountOrder) {
        if(documentAccountOrders == null)
            documentAccountOrders = new ArrayList<>();

        return documentAccountOrders.add(documentAccountOrder);
    }

    // 결재양식 필드 추가
    @ManyToOne
    @JoinColumn(name = "template_id")
    private DocumentTemplate template;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentFile> documentFiles = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentCourseAccount> documentCourseAccountList = new ArrayList<>();

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
