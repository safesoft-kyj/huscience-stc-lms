package com.dtnsm.lms.controller;

import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.domain.Course;
import com.dtnsm.lms.domain.Survey;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.service.BinderLogService;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.util.XdocUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class CourseBinderController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseBinderController.class);

    @Autowired
    BinderLogService binderLogService;

    @Autowired
    SignatureRepository signatureRepository;

    public FileUploadProperties prop;

    CourseBinderController(FileUploadProperties prop){
        this.prop = prop;
    };

    @GetMapping("/xdoc/create")
    public String createDocx() {

        XdocUtil.createXdocToDocx(prop, "Certificate.docx", signatureRepository);
        //XdocUtil.createXdocToDocx(prop, "Certificate.docx", signatureRepository);
//        XdocUtil.createXdocToDocx(prop, "Certificate.docx", signatureRepository);
        //XdocUtil.testXdocToPdf(prop, "Certificate.docx_out.docx", signatureRepository);


        return "success";
    }

}
