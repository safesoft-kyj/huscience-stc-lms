package com.dtnsm.lms.controller.RestController;

import com.dtnsm.common.entity.TrainingRecord;
import com.dtnsm.common.entity.constant.TrainingRecordStatus;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.TrainingRecordRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseTrainingLog;
import com.dtnsm.lms.domain.DTO.CommonUtilities;
import com.dtnsm.lms.domain.TrainingRecordReview;
import com.dtnsm.lms.domain.constant.TrainingRecordReviewStatus;
import com.dtnsm.lms.domain.datasource.CertificateSource;
import com.dtnsm.lms.domain.datasource.EmployeeTrainingLogSource;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.TrainingRecordReviewRepository;
import com.dtnsm.lms.service.BinderLogService;
import com.dtnsm.lms.service.CertificateFileService;
import com.dtnsm.lms.service.CourseCertificateService;
import com.dtnsm.lms.util.DocumentConverter;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.assembly.DocumentAssembler;
import com.groupdocs.assembly.DocumentAssemblyOptions;
import com.groupdocs.conversion.handler.ConvertedDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@Slf4j
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

    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    @Autowired
    private TrainingRecordReviewRepository trainingRecordReviewRepository;

    @Autowired
    private DocumentConverter documentConverter;

    public FileUploadProperties prop;

    CourseBinderRestController(FileUploadProperties prop){
        this.prop = prop;
    };

    @PostMapping("/api/binder/log/create")
    public Map<String, String> createBindLog() {
        Map<String, String> returnValue = new HashMap<>();
        String userId = SessionUtil.getUserId();
//        Optional<TrainingRecordReview> optionalTrainingRecordReview = trainingRecordReviewRepository.findTop1ByAccountOrderByIdDesc(SessionUtil.getUserDetail().getUser());
//        if(optionalTrainingRecordReview.isPresent()) {
//            if(optionalTrainingRecordReview.get().getStatus() == TrainingRecordReviewStatus.REQUEST) {
//                returnValue.put("message", "검토 요청 중인 정보가 존재합니다. 검토 완료 후 요청해 주세요.");
//                return returnValue;
//            }
//        }
        Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findTop1ByUsernameOrderByIdDesc(userId);
        if(optionalTrainingRecord.isPresent()) {
            TrainingRecord trainingRecord = optionalTrainingRecord.get();
            if(TrainingRecordStatus.REVIEW == trainingRecord.getStatus()) {
                returnValue.put("message", "현재 검토 진행 중 입니다. 완료 후 배포 가능 합니다.");
                return returnValue;
            } else {
                create(trainingRecord.getStatus() == TrainingRecordStatus.REVIEWED ? null : trainingRecord.getId());
            }
        } else {
            create(null);
        }

        returnValue.put("message", "Training Log 배포 완료 되었습니다.");
        return returnValue;
    }


//    @PostMapping("/api/binder/certi/create")
//    public String createBindCertification(@RequestParam("docId") long docId, RedirectAttributes attributes) {
//        String userId = SessionUtil.getUserId();
//        Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findTop1ByUsernameOrderByIdDesc(userId);
//        if(optionalTrainingRecord.isPresent()) {
//            TrainingRecord trainingRecord = optionalTrainingRecord.get();
//            if(TrainingRecordStatus.REVIEW == trainingRecord.getStatus()) {
//                attributes.addFlashAttribute("message", "현재 검토 진행 중 입니다. 완료 후 배포 가능 합니다.");
//                return "redirect:/training/trainingLog";
//            } else {
//                create(docId, trainingRecord.getStatus() == TrainingRecordStatus.REVIEWED ? 0 : trainingRecord.getId());
//            }
//        } else {
//            create(docId, 0);
//        }
//
//        attributes.addFlashAttribute("message", "Training Log 배포 완료 되었습니다.");
//        return "redirect:/training/trainingLog";
//    }

    private boolean create(Integer trainingRecordId) {
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
        String outputEmployeeLogPdfFileName = String.format("%s_%s_tm.pdf", timestamp, userId);
        String outputEmployeeLogDocxFileName = String.format("%s_%s_tm.docx", timestamp, userId);

        // Employee Training Log Full Path
//        InputStream is = CurriculumVitaeReportService.class.getResourceAsStream(srcEmployeeLogTemplateFileName);
//        String srcEmployeeLogFilePath = sourceRootFoloer + srcEmployeeLogTemplateFileName;
        InputStream logSource = CurriculumVitaeReportService.class.getResourceAsStream(srcEmployeeLogTemplateFileName);
        String outputEmployeeLogFilePath = outputRootFoloer + outputEmployeeLogPdfFileName;
        String outputEmployeeLogDocxFilePath = outputRootFoloer + outputEmployeeLogDocxFileName;

//        Path sourcePath = Paths.get(sourceRootFoloer, srcEmployeeLogTemplateFileName);
//        Path outputPath = Paths.get(outputRootFoloer, outputEmployeeLogFileName);

        // 수료증 Full Path
        String outputCertificationFilePath = outputRootFoloer + outputCertificationFileName;
//        String outputLogFilePath = outputRootFoloer + outputEmployeeLogFileName;

        try {
            // Employee Training Log Data
            EmployeeTrainingLogSource trainingLogSource = binderLogService.getEmployeeTrainingLog(userId, timestamp);

//            log.info("trainingLogSource : {}", trainingLogSource);
            // Employee Training Log Data를 source에 추가한다.
            DataSourceInfo dataSourceInfo = new DataSourceInfo(trainingLogSource, null);

            DocumentAssembler assembler = new DocumentAssembler();
            assembler.getKnownTypes().add(EmployeeTrainingLogSource.class);
            assembler.getKnownTypes().add(CourseTrainingLog.class);
            assembler.getKnownTypes().add(Account.class);

//            assembler.setOptions(DocumentAssemblyOptions.UPDATE_FIELDS_AND_FORMULAS);
//            assembler.assembleDocument(in, out, new DataSourceInfo( accountList, "accountList"));

            // 2. 수료증 파일 병합(파일생성)
            log.info("==> Certi PDF 파일 생성. {}", outputCertificationFilePath);
            String certHtmlContent = certificateFileService.createCertificateFileMerge(userId, timestamp, outputCertificationFilePath);

            // 1. Employee Training Log File 생성
//            log.info("sourcePath : {}", srcEmployeeLogFilePath);
//            log.info("outputPath : {}", outputEmployeeLogFilePath);

            assembler.setOptions(DocumentAssemblyOptions.INLINE_ERROR_MESSAGES);

            log.info("==> Word 데이터 바인딩");
            log.info("outputEmployeeLogFilePath : {}", outputEmployeeLogFilePath);

            boolean result = assembler.assembleDocument(logSource, new FileOutputStream(outputEmployeeLogDocxFilePath), dataSourceInfo);
            log.info("<== 바인딩된 Word 파일 생성[{}] : {}", outputEmployeeLogDocxFilePath, result);


//            output.flush();
            if(result) {
                String html = documentConverter.toHTMLString(outputEmployeeLogDocxFilePath);
                log.debug("Word to HTML : {}", html);

                saveTrainingRecord(trainingRecordId, userId, outputCertificationFileName, html, outputCertificationFileName, certHtmlContent);

                //word to pdf
                assembler.assembleDocument(outputEmployeeLogDocxFilePath, outputEmployeeLogFilePath , dataSourceInfo);
            }

            return true;
        } catch (Exception exp) {
            System.out.println("Exception: " + exp.getMessage());
            return false;
        }

    }

    public void saveTrainingRecord(Integer trainingRecordId, String userId, String outputFilename, String tmHtml, String certFileName, String certHtml) {
        TrainingRecord trainingRecord;
        if(ObjectUtils.isEmpty(trainingRecordId)) {
            trainingRecord = new TrainingRecord();
            trainingRecord.setUsername(userId);
            trainingRecord.setStatus(TrainingRecordStatus.PUBLISHED);
            trainingRecord.setTmStatus(TrainingRecordStatus.PUBLISHED);
        } else {
            trainingRecord = trainingRecordRepository.findById(trainingRecordId).get();
        }
        trainingRecord.setTmFileName(outputFilename);
        trainingRecord.setTmHtmlContent(tmHtml);

        trainingRecord.setTmCertFileName(certFileName);
        trainingRecord.setTmCertHtmlContent(certHtml);

        trainingRecordRepository.save(trainingRecord);
    }
}
