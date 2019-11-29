package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="el_course_certificate_log")
public class CourseCertificateLog extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue
    private Integer id;

    // 수료증 번호
    private String cerNo;
    private String sopName;
    private String sopEffectiveDate;

    @ManyToOne
    @JoinColumn(name = "doc_id")
    private CourseAccount courseAccount;

    @ManyToOne
    @JoinColumn(name = "cer_manager1", columnDefinition="VARCHAR(30)")
    private Account cerManager1;

    @ManyToOne
    @JoinColumn(name = "cer_manager2", columnDefinition="VARCHAR(30)")
    private Account cerManager2;
}
