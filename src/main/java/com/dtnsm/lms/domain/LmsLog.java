package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="el_lms_log")
@Audited(withModifiedFlag = true)
public class LmsLog extends AuditorCreateEntity<String> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // Form Name
    @Column(length = 100)
    private String formName;

    // 레벨
    @Column(length = 10)
    private String lvl;

    // 메세지
    @Column(length = 5000)
    private String message;
}
