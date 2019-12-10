package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Data
@Table(name="el_customer")
public class Customer extends AuditorEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 10, nullable = false)
    private String condate;

    @Column(length = 50, nullable = false)
    private String condition;

    @Column(length = 100, nullable = false)
    private String homepage;

    @ColumnDefault("0")
    private int viewCnt;

}
