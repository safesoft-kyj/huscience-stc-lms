package com.dtnsm.lms.mybatis.dto;

import lombok.Data;

import java.util.List;

@Data
public class CVFindParam {
    private int career;
    private String ta;
    private String indication;
    private List<String> usernameList;
}
