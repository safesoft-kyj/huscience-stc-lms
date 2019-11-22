package com.dtnsm.lms.mybatis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Alias("ReportForm1")
public class ReportForm1 {
    private int id;
    private String title;
    private int ex1;
    private int ex2;
    private int ex3;
    private int ex4;
    private int ex5;
    private int ex6;
    private int ex7;
    private int ex8;
    private int ex9;
    private int ex10;
    private int total;
}
