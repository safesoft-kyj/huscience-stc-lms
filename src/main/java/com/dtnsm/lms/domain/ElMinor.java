package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.AuditorEntity;
import com.dtnsm.lms.domain.ElMajor;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "el_minor")
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = AuditorEntity.class)
public class ElMinor extends AuditorEntity<String> {

//    @EmbeddedId
//    private ElMinorId elMinorId;

    @Id
    @Column(name="minor_cd", length = 10, nullable = false)
    private String minorCd;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name="major_cd") , name = "major_cd", referencedColumnName = "major_cd", nullable = false)
    private ElMajor elMajor;

    @Column(name="minor_nm", length = 30, nullable = false)
    private String minorNm;

    @ColumnDefault("1")
    @Column(nullable = false)
    private int seq;

    public void setMinorCd(String minorCd) {
        this.minorCd = minorCd;
    }

    public void setElMajor(ElMajor elMajor) {
        if(this.elMajor != null)
            this.elMajor.getMinors().remove(this);
        this.elMajor = elMajor;
        elMajor.getMinors().add(this);
    }

    public void setMinorNm(String minorNm) {
        this.minorNm = minorNm;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
