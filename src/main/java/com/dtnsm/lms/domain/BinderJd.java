package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="binder_jd")
public class BinderJd extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    // 등록일
    @Column(length = 10)
    private String regDate;

    @Column(length = 100)
    private String signature;

    @Column(length = 100)
    private String signature2;

    private String isActive = "0";

    @ManyToOne
    @JoinColumn(name = "version_id")
    private JobDescriptionVersion jdVersion;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition="VARCHAR(30)")
    private Account account;
}
