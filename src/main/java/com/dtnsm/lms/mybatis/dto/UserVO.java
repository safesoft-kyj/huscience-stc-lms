package com.dtnsm.lms.mybatis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("vw_user")
public class UserVO {
    private String userId;
    private String korName;
    private String engName;
    private String password;
    private String orgCode;
    private String orgDepart;
    private String comPosition;
    private String duty;
    private String comJob;
    private String email;
    private String indate;
    private String outdate;
    private String approSign;
    private boolean isuse;
}
