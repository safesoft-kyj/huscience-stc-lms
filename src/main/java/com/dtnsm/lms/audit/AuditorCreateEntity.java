package com.dtnsm.lms.audit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditorCreateEntity<String>
{
    @CreatedBy
    @Column(name = "insert_user_id", length = 30,updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "insert_dt", updatable = false)
    private Date createdDate;
}
