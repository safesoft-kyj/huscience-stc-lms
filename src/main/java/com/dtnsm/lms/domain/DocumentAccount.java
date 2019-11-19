package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="el_document_account")
//@IdClass(DocumentAccountId.class)
public class DocumentAccount extends AuditorEntity<String> {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;

    @Column(length = 10)
    private String requestDate;

    // 신청일
    private Date fWdate;

    // 총결재자 수
    private int fFinalCount = 0;

    // 현재결재자 순번
    private int fCurrSeq = 0;

    // 전자 결재 상태 : 0: 진행중, 1: 승인, 2:기각
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fStatus = "0";

    @OneToMany(mappedBy = "documentAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentAccountOrder> documentAccountOrders = new ArrayList<>();

    // 최종 종결 유무
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String isCommit = "0";

    public boolean addCourseAccountOrder(DocumentAccountOrder documentAccountOrder) {
        if(documentAccountOrders == null)
            documentAccountOrders = new ArrayList<>();

        return documentAccountOrders.add(documentAccountOrder);
    }

}
