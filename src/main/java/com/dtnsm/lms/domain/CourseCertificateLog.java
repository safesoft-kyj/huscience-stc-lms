package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name="el_course_certificate_log")
public class CourseCertificateLog extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    // 수료증 번호
    private String cerNo;
    private String sopName;
    private String sopEffectiveDate;
    private Date CerDate;


    // 강사 정보
    private String cerManagerText1;

    // 대표자 정보
    private String cerManagerText2;


    @OneToOne
    @JoinColumn(name = "doc_id")
    private CourseAccount courseAccount;

    @ManyToOne
    @JoinColumn(name = "cer_manager1", columnDefinition="VARCHAR(30)")
    private Account cerManager1;

    @Lob
    @Column(name="cer_manager_sign1")
    private String cerManagerSign1;

    @ManyToOne
    @JoinColumn(name = "cer_manager2", columnDefinition="VARCHAR(30)")
    private Account cerManager2;

    @Lob
    @Column(name="cer_manager_sign2")
    private String cerManagerSign2;

    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;
}
