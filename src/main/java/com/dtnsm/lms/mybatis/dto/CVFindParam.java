package com.dtnsm.lms.mybatis.dto;

import lombok.Data;

import java.util.List;

@Data
public class CVFindParam {
    private int career;
    private String ta;
    private String indication;
    private String name;
    private List<String> usernameList;
    private int empStatus; // 0:퇴사, 1:재직, 2:전체검색
}
