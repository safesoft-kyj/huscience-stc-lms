package com.dtnsm.lms.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo {
    private String pageId;
    private String pageTitle;
    private String parentId;
    private String parentTitle;
}
