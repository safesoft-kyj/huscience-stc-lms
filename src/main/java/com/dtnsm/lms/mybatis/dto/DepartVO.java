package com.dtnsm.lms.mybatis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("vw_depart")
public class DepartVO {
    private String orgCode;
    private String pOrgCode;
    private int orgLevel;
    private String orgDepart;
    private int orgOrder;
}
