package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Data
@Table(name="el_course_manager")
@Audited(withModifiedFlag = true)
public class CourseManager extends AuditorCreateEntity<String> {

    @Id
    @Column(length = 30, name = "user_id")
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "userId")
    private Account account;

//    @Id
//    @Column(name = "user_id", length = 30)
//    private String userId;

    @Column(length = 10)
    private String registerDate;

    // 1: 기본 관리자
    @ColumnDefault("0")
    private int isActive = 0;
}
