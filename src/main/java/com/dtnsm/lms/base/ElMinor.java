package com.dtnsm.lms.base;

import com.dtnsm.lms.audit.AuditorEntity;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "el_minor")
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
