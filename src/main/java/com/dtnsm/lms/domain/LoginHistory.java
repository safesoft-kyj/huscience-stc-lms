package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name="el_login_history")
@Audited(withModifiedFlag = true)
public class LoginHistory {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String userId;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String engName;

    @Column(length = 100)
    private String ipAddress;

    @CreationTimestamp()
    private Date loginDateTime;

    private Date logoutDateTime;
}