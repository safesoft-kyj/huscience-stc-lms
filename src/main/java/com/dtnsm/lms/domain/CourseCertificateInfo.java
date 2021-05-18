package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Data
@Table(name="el_course_certificate_info")
@Audited(withModifiedFlag = true)
public class CourseCertificateInfo extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String sopName;
    private String sopEffectiveDate;

    @ManyToOne
    @JoinColumn(name = "cer_manager1",columnDefinition="VARCHAR(30)")
    private Account cerManager1;

    @ManyToOne
    @JoinColumn(name = "cer_manager2",columnDefinition="VARCHAR(30)")
    private Account cerManager2;

    // 강사 정보
    private String cerManagerText1;

    // 대표자 정보
    private String cerManagerText2;

    // 1: 기본 수료증
    @ColumnDefault("0")
    private int isActive = 0;
}
