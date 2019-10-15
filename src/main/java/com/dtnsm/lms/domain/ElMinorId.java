package com.dtnsm.lms.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class ElMinorId implements Serializable {

    // ElbMinor 클래스 @MapsId("major_cd")로 매핑
    @Column(name="major_cd", length = 5)
    private String majorCd;

    @Column(name="minor_cd",length = 10)
    private String minorCd;

}
