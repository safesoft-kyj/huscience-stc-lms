package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="el_document_account_order")
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class DocumentAccountOrder extends AuditorEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 컴멘트
    @Column(length = 500)
    private String fnComment;

    // 0:초기, 1:결재, 2. 합의, 3:확인
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fnKind = "0";

    // 결재순번
    @ColumnDefault("0")
    private int fnSeq = 0;

    // 다음결재자 유무 :0, 1
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fnNext = "0";

    // 결재일
    private Date fnDate;

    @Lob
    @Column
    private String signature;

    @ManyToOne
    @JoinColumn(name = "f_user_id",columnDefinition="VARCHAR(30)")
    private Account fnUser;

    // 상태 : 0: 미결, 1: 승인, 2:기각
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fnStatus = "0";

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
}
