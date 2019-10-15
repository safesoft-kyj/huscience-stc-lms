package com.dtnsm.lms.mybatis.dto;

import lombok.*;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
public class CalendarVO {
    private String title;
    private String start;
    private String end;
    private String url;
    private String color;
    private String textColor;
    private boolean allDay = true;
}
