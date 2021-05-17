package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "el_major")
@Audited(withModifiedFlag = true)
public class ElMajor extends AuditorEntity<String> {
    @Id
    @Column(name="major_cd", length = 5, nullable = false)
    private String majorCd;

    @Column(name="major_nm", length = 30, nullable = false)
    private String majorNm;

    @Column(length = 1, nullable = false)
    private String type;

    @OneToMany(mappedBy = "elMajor")
    @AuditMappedBy(mappedBy = "elMajor")
    private Set<ElMinor> minors = new HashSet<>();

    public void add(ElMinor minor) {
        minor.setElMajor(this);
        this.minors.add(minor);
    }

}
