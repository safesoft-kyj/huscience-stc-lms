package com.dtnsm.lms.customer;

import com.dtnsm.lms.audit.AuditorEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class ElCustomer extends AuditorEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 8, nullable = false)
    private String condate;

    @Column(length = 50, nullable = false)
    private String condition;

     @Column(length = 100, nullable = false)
    private String homepage;
}
