package com.dtnsm.lms.base;

import com.dtnsm.lms.audit.AuditorEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "el_major")
public class ElMajor extends AuditorEntity<String> {
    @Id
    @Column(name="major_cd", length = 5, nullable = false)
    private String majorCd;

    @Column(name="major_nm", length = 30, nullable = false)
    private String majorNm;

    @Column(length = 1, nullable = false)
    private String type;

    @OneToMany(mappedBy = "elMajor")
    private Set<ElMinor> minors = new HashSet<>();

    public void add(ElMinor minor) {
        minor.setElMajor(this);
        this.minors.add(minor);
    }

}
