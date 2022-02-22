package com.dtnsm.lms.domain;



import com.dtnsm.lms.auth.AuditorCreateEntity;
import lombok.Data;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_border_master")
@Audited(withModifiedFlag = true)
public class BorderMaster extends AuditorCreateEntity<String> {
    @Id
    @Column(length = 10, nullable = false)
    private String id;

    //@NotEmpty(message = "No title")
    @Column(length = 100, nullable = false)
    private String borderName;

    @OneToMany(mappedBy = "borderMaster",cascade = CascadeType.ALL)
    @AuditMappedBy(mappedBy = "borderMaster")
    private List<Border> borders = new ArrayList<>();

    @Column(length = 10, nullable = false)
    private String minorCd;

    @Column(length = 1, nullable = false)
    private String isMail = "N";

//
//    @Transient
//    private String minorName;

    public BorderMaster(){}

    public BorderMaster(String borderName) {
        this.borderName = borderName;
    }
}
