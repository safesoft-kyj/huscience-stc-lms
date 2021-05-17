package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorCreateEntity;
import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_border_account")
@Audited(withModifiedFlag = true)
public class BorderViewAccount extends AuditorCreateEntity<String> {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    // 게시판
    @ManyToOne
    @JoinColumn(name = "border_id")
    private Border border;

    // 사용자
    @ManyToOne
    @JoinColumn(name = "user_id",columnDefinition="VARCHAR(30)")
    @NotAudited
    private Account account;

    public BorderViewAccount(){}

    public BorderViewAccount(Border border, Account account) {
        this.border = border;
        this.account = account;
    }
}
