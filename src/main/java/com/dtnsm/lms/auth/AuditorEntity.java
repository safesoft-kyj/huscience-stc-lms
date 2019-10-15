package com.dtnsm.lms.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditorEntity<String>{

    @CreatedBy
    @Column(name = "insert_user_id", length = 30,updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "insert_dt", updatable = false)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "update_user_id", length = 30)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "update_dt", updatable = true)
    private Date lastModifiedDate;
}
