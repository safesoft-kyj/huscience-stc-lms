package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.LmsAlarmGubun;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name="el_lms_notification")
public class LmsNotification extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 교육과정 진행 상태
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private LmsAlarmGubun alarmGubun;


    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 255, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    public LmsNotification(LmsAlarmGubun alarmGubun, String title, String content, Account account) {
        this.alarmGubun = alarmGubun;
        this.title = title;
        this.content = content;
        this.account = account;
    }
}
