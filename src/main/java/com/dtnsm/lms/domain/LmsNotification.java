package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="el_notification")
public class LmsNotification extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue
    private long id;

    @Column(length = 255, nullable = false)
    private String title;

    // Completion Date
    @Column(length = 11, nullable = false)
    private String applyDate;

    @Column(name="training_hr", columnDefinition = "numeric(5,2)")
    private float trainingHr;

    @Column(name="organization", length = 50)
    private String Organization;

    // Completion DateTime
    private Date applyDateTime;

    // Section Action Id
    @ManyToOne
    @JoinColumn(name = "sec_act_id")
    private CourseSectionAction courseSectionAction;

    //
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;


    

}
