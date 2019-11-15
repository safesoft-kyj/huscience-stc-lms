package com.dtnsm.lms.controller;


import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.util.XdocUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class XdocReportController {

    public FileUploadProperties prop;

    XdocReportController(FileUploadProperties prop){
        this.prop = prop;
    };

    @GetMapping("/xdoc/create")
    public String createDocx() {

        XdocUtil.createXdocToDocx(prop, "Certificate.docx");

        return "success";
    }
}
