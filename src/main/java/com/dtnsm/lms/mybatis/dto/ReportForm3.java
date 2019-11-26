package com.dtnsm.lms.mybatis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Alias("ReportForm1")
public class ReportForm3 {
    private Long questionId;
    private String question;
    private String userAnswer;
}
