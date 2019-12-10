package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="el_course_account_order")
public class CourseAccountOrder extends AuditorEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 컴멘트
    private String fComment;

    // 0:초기, 1:결재(팀장), 2. 합의(관리자)
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fKind = "0";

    // 결재순번
    @ColumnDefault("0")
    private int fSeq = 0;

    // 다음결재자 유무 :0, 1
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fNext = "0";

    @Lob
    @Column
    private String signature;

    // 결재일
    private Date fDate;

    @ManyToOne
    @JoinColumn(name = "f_user_id",columnDefinition="VARCHAR(30)")
    private Account  fUser;

    // 상태 : 0: 미결, 1: 승인, 2:기각
    @Column(length = 1)
    @ColumnDefault("'0'")
    private String fStatus = "0";

    @ManyToOne
    @JoinColumn(name = "doc_no")
    private CourseAccount courseAccount;
}
