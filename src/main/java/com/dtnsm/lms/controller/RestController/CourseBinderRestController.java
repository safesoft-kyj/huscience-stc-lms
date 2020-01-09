package com.dtnsm.lms.controller.RestController;

import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseTrainingLog;
import com.dtnsm.lms.domain.DTO.CommonUtilities;
import com.dtnsm.lms.domain.datasource.CertificateSource;
import com.dtnsm.lms.domain.datasource.EmployeeTrainingLogSource;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.service.BinderLogService;
import com.dtnsm.lms.service.CertificateFileService;
import com.dtnsm.lms.service.CourseCertificateService;
import com.dtnsm.lms.util.SessionUtil;
import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.assembly.DocumentAssembler;
import com.groupdocs.assembly.DocumentAssemblyOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class CourseBinderRestController {

//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourseBinderController.class);

    @Autowired
    BinderLogService binderLogService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    SignatureRepository signatureRepository;

    @Autowired
    CourseCertificateService courseCertificateService;

    @Autowired
    private CertificateFileService certificateFileService;

    public FileUploadProperties prop;

    CourseBinderRestController(FileUploadProperties prop){
        this.prop = prop;
    };

    @PostMapping("/api/binder/log/create")
    public boolean createBindLog() {

        // GroupDoc 라이센스
        // TODO : groupdoc License 호출
        CommonUtilities.applyLicense();

        // 파일 기본 경로
        String sourceRootFoloer = prop.getXdocUploadDir() + "Data//Storage//";
        String outputRootFoloer = prop.getXdocUploadDir() + "Data//Output//";



        String userId = SessionUtil.getUserId();
        long timestamp = System.currentTimeMillis();

        // Employee Training Log Template 파일 및 출력 파일 지정
        String srcEmployeeLogTemplateFileName = "etl_tm.docx";
        String outputCertificationFileName = String.format("%s_%s_certi.pdf", timestamp, userId);
        String outputEmployeeLogFileName = String.format("%s_%s_tm.pdf", timestamp, userId);

        // Employee Training Log Full Path
        String srcEmployeeLogFilePath = sourceRootFoloer + srcEmployeeLogTemplateFileName;
        String outputEmployeeLogFilePath = outputRootFoloer + outputEmployeeLogFileName;

        Path sourcePath = Paths.get(sourceRootFoloer, srcEmployeeLogTemplateFileName);
        Path outputPath = Paths.get(outputRootFoloer, outputEmployeeLogFileName);

        // 수료증 Full Path
        String outputCertificationFilePath = outputRootFoloer + outputCertificationFileName;

        try {
            // Employee Training Log Data
            EmployeeTrainingLogSource trainingLogSource = binderLogService.getEmployeeTrainingLog(userId, timestamp);

            // Employee Training Log Data를 source에 추가한다.
            DataSourceInfo dataSourceInfo = new DataSourceInfo(trainingLogSource, null);

            DocumentAssembler assembler = new DocumentAssembler();
            assembler.getKnownTypes().add(EmployeeTrainingLogSource.class);
            assembler.getKnownTypes().add(CourseTrainingLog.class);
            assembler.getKnownTypes().add(Account.class);

//            assembler.setOptions(DocumentAssemblyOptions.UPDATE_FIELDS_AND_FORMULAS);
//            assembler.assembleDocument(in, out, new DataSourceInfo( accountList, "accountList"));

            // 2. 수료증 파일 병합(파일생성)
            certificateFileService.createCertificateFileMerge(userId, timestamp, outputCertificationFilePath);

            // 1. Employee Training Log File 생성
            System.out.println("sourcePath : " + sourcePath.toString());
            System.out.println("outputPath : " + outputPath.toString());

            assembler.setOptions(DocumentAssemblyOptions.INLINE_ERROR_MESSAGES);

            assembler.assembleDocument(sourcePath.toString(), outputPath.toString() , dataSourceInfo);
//            assembler.assembleDocument(srcEmployeeLogFilePath, outputEmployeeLogFilePath , dataSourceInfo);
//            assembler.assembleDocument(CommonUtilities.getDataPath(srcEmployeeLogTemplateFileName)
//                    , CommonUtilities.getOutPath(outputEmployeeLogFileName) , dataSourceInfo);

        } catch (Exception exp) {
            System.out.println("Exception: " + exp.getMessage());
        }

        return true;
    }


    @PostMapping("/api/binder/certi/create")
    public boolean createBindCertification(@RequestParam("docId") long docId) {

        // GroupDoc 라이센스
        // TODO : groupdoc License 호출
        CommonUtilities.applyLicense();

        // 파일 기본 경로
        String sourceRootFoloer = prop.getXdocUploadDir() + "Data//Storage//";
        String outputRootFoloer = prop.getXdocUploadDir() + "Data//Output//";

        String userId = SessionUtil.getUserId();
        long timestamp = System.currentTimeMillis();

        // Employee Training Log Template 파일 및 출력 파일 지정
        String srcCertificationTemplateFileName = "etl_certi.docx";
        String outputCertificationFileName = String.format("%s_%s_certi.pdf", timestamp, userId);

        // 수료증 Full Path
        String srcCertificationFilePath = sourceRootFoloer + srcCertificationTemplateFileName;
        String outputCertificationFilePath = outputRootFoloer + outputCertificationFileName;

        try {
            // Employee Training Log Data
            CertificateSource certificateSource = courseCertificateService.getCertificateSource(userId, timestamp, docId);

            // Employee Training Log Data를 source에 추가한다.
            DataSourceInfo dataSourceInfo = new DataSourceInfo(certificateSource, null);

            DocumentAssembler assembler = new DocumentAssembler();
//            assembler.getKnownTypes().add(EmployeeTrainingLogSource.class);
//            assembler.getKnownTypes().add(CourseTrainingLog.class);
//            assembler.getKnownTypes().add(Account.class);

            assembler.setOptions(DocumentAssemblyOptions.INLINE_ERROR_MESSAGES);
//            assembler.assembleDocument(in, out, new DataSourceInfo( accountList, "accountList"));

            // 1. Employee Training Log File 생성
            assembler.assembleDocument(srcCertificationFilePath, outputCertificationFilePath , dataSourceInfo);

        } catch (Exception exp) {
            System.out.println("Exception: " + exp.getMessage());
        }

        return true;
    }
}
