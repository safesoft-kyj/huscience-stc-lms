package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.domain.constant.LmsAlarmGubun;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name="el_lms_notification")
@Audited(withModifiedFlag = true)
public class LmsNotification extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 교육과정 진행 상태
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private LmsAlarmGubun alarmGubun;

    // C:과정, D:전자결재

    @Column(length = 1, nullable = false)
//    @Transient
    private String gubun;


    @Column(length = 1000, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @NotAudited
    private Course course;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    public LmsNotification(LmsAlarmGubun alarmGubun, String title, String content, Account account) {
        this.alarmGubun = alarmGubun;
        this.title = title;
        this.content = content;
        this.account = account;
    }
}
