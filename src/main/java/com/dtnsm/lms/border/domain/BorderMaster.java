package com.dtnsm.lms.border.domain;



import com.dtnsm.lms.audit.AuditorCreateEntity;
import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="el_border_master")
public class BorderMaster extends AuditorCreateEntity<String> {
    @Id
    @Column(length = 10, nullable = false)
    private String id;

    //@NotEmpty(message = "No title")
    @Column(length = 100, nullable = false)
    private String borderName;

    @OneToMany(mappedBy = "borderMaster", cascade = CascadeType.ALL, orphanRemoval = true)
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
