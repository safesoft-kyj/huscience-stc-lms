package com.dtnsm.lms.mybatis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepartTreeVO {


    private String id;         // 부모

    @JsonProperty("pId")
    private String pId;        // 자식
    private String name;       // 트리이름
    private String email;
    private boolean open;       // 트리 OPEN 여부, true/false

    @JsonProperty("isParent")
    private boolean isParent;   // 부모 여부, true/false

    private String web="";        // 링크 URL

    @JsonProperty("isEmp")
    private boolean isEmp;
}