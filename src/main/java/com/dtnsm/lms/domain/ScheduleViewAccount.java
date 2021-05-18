package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Data
@Table(name="el_schedule_account")
@Audited(withModifiedFlag = true)
public class ScheduleViewAccount extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 게시판
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // 사용자
    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    private Account account;

    public ScheduleViewAccount(){}

    public ScheduleViewAccount(Schedule schedule, Account account) {
        this.schedule = schedule;
        this.account = account;
    }
}
