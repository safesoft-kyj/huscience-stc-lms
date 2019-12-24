package com.dtnsm.lms.controller;

import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseTrainingLog;
import com.dtnsm.lms.domain.DTO.CommonUtilities;
import com.dtnsm.lms.domain.DocDataSource;
import com.dtnsm.lms.domain.DocSource;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.service.BinderLogService;
import com.dtnsm.lms.util.SessionUtil;
import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.assembly.DocumentAssembler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
public class CourseBinderController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseBinderController.class);

    @Autowired
    BinderLogService binderLogService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    SignatureRepository signatureRepository;


    public FileUploadProperties prop;

    CourseBinderController(FileUploadProperties prop){
        this.prop = prop;
    };

    @GetMapping("/xdoc/create")
    public String createDocx() {

//        XdocUtil.createXdocToDocx(prop, "Certificate.docx", signatureRepository);
        //XdocUtil.createXdocToDocx(prop, "Certificate.docx", signatureRepository);
//        XdocUtil.createXdocToDocx(prop, "Certificate.docx", signatureRepository);
        //XdocUtil.testXdocToPdf(prop, "Certificate.docx_out.docx", signatureRepository);


//        String srcDocument = prop.getXdocUploadDir() + "Update field.docx";
//        String docReport = prop.getXdocUploadDir() + "Update field_report.docx";

        String userId = SessionUtil.getUserId();



        String srcDocument = "etl_tm.docx";
        String docReport = "etl_tm_" + userId + ".docx";
        try {

            DocSource docDataSource = new DocSource("DocDataSource");

            // 사용자별 로그를 가지고 온다.
            List<CourseTrainingLog> courseTrainingLogs = binderLogService.getAllByUser(userId);
            docDataSource.setAccount(userService.getAccountByUserId(userId));
            docDataSource.setCourseTrainingLogs(courseTrainingLogs);

            // 사용자별 로그를 data source에 추가한다.
            DataSourceInfo dataSourceInfo = new DataSourceInfo(docDataSource, null);

            DocumentAssembler assembler = new DocumentAssembler();
            assembler.getKnownTypes().add(DocSource.class);
            assembler.getKnownTypes().add(CourseTrainingLog.class);
            assembler.getKnownTypes().add(Account.class);

//            assembler.setOptions(DocumentAssemblyOptions.UPDATE_FIELDS_AND_FORMULAS);



//            assembler.assembleDocument(in, out, new DataSourceInfo( accountList, "accountList"));

            assembler.assembleDocument(CommonUtilities.getDataPath(srcDocument), CommonUtilities.getOutPath(docReport) , dataSourceInfo);
        } catch (Exception exp) {
            System.out.println("Exception: " + exp.getMessage());
        }

        return "success";
    }

//    @GetMapping("/xdoc/create2")
//    public String createDocx2() {
//
//        String srcDocument = prop.getXdocUploadDir() + "Update field.docx";
//        String docReport = prop.getXdocUploadDir() + "Update field_report.docx";
//        try {
//            BusinessEntities.Manager manager = new DataStorage().getManagers().iterator().next();
//            DocumentAssembler assembler = new DocumentAssembler();
//
//            InputStream in = new FileInputStream(srcDocument);
//
//            OutputStream out = new FileOutputStream(new File(docReport));
//
//            assembler.setOptions(DocumentAssemblyOptions.UPDATE_FIELDS_AND_FORMULAS);
//            assembler.assembleDocument(srcDocument, docReport, new DataSourceInfo( new DataStorage(), null));
//        } catch (Exception exp) {
//            System.out.println("Exception: " + exp.getMessage());
//        }
//
//        return "success";
//    }



}
