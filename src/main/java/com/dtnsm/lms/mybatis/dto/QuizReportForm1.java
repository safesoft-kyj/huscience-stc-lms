package com.dtnsm.lms.mybatis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizReportForm1 {
    //과정코드
    private Long courseId;

    // 문제 ID
    private Long questionId;
    // 문제
    private String question;

    // 시험횟수
    private int cnt;
    // 오답여부(1:오답, 0:정답)
    private int failCount;

    // 오답률
    private int failRate;
}
