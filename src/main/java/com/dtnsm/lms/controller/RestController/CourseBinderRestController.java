package com.dtnsm.lms.controller.RestController;

import com.dtnsm.common.entity.QTrainingRecord;
import com.dtnsm.common.entity.TrainingRecord;
import com.dtnsm.common.entity.constant.TrainingRecordStatus;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.TrainingRecordRepository;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseTrainingLog;
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
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
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
//                returnValue.put("message", "?????? ?????? ?????? ????????? ???????????????. ?????? ?????? ??? ????????? ?????????.");
//                return returnValue;
//            }
//        }
        Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findTop1ByUsernameOrderByIdDesc(userId);
        if(optionalTrainingRecord.isPresent()) {
            TrainingRecord trainingRecord = optionalTrainingRecord.get();
            if(TrainingRecordStatus.REVIEW == trainingRecord.getStatus()) {
                returnValue.put("message", "?????? ?????? ?????? ??? ?????????. ?????? ??? ?????? ?????? ?????????.");
                return returnValue;
            } else {
                create(trainingRecord.getStatus() == TrainingRecordStatus.REVIEWED ? null : trainingRecord.getId());
            }
        } else {
            create(null);
        }

        returnValue.put("message", "Training Log ?????? ?????? ???????????????.");
        return returnValue;
    }


//    @PostMapping("/api/binder/certi/create")
//    public String createBindCertification(@RequestParam("docId") long docId, RedirectAttributes attributes) {
//        String userId = SessionUtil.getUserId();
//        Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findTop1ByUsernameOrderByIdDesc(userId);
//        if(optionalTrainingRecord.isPresent()) {
//            TrainingRecord trainingRecord = optionalTrainingRecord.get();
//            if(TrainingRecordStatus.REVIEW == trainingRecord.getStatus()) {
//                attributes.addFlashAttribute("message", "?????? ?????? ?????? ??? ?????????. ?????? ??? ?????? ?????? ?????????.");
//                return "redirect:/training/trainingLog";
//            } else {
//                create(docId, trainingRecord.getStatus() == TrainingRecordStatus.REVIEWED ? 0 : trainingRecord.getId());
//            }
//        } else {
//            create(docId, 0);
//        }
//
//        attributes.addFlashAttribute("message", "Training Log ?????? ?????? ???????????????.");
//        return "redirect:/training/trainingLog";
//    }

    private boolean create(Integer trainingRecordId) {
        // GroupDoc ????????????
        // TODO : groupdoc License ??????
//        CommonUtilities.applyLicense();

        // ?????? ?????? ??????
//        String sourceRootFoloer = prop.getXdocUploadDir() + "Data//Storage//";
        String outputPath = prop.getBinderDir();



        String userId = SessionUtil.getUserId();
        long timestamp = System.currentTimeMillis();

        log.info("@userid : {} ???????????? ?????? ????????? ?????? ??????", userId);
        // Employee Training Log Template ?????? ??? ?????? ?????? ??????
        String srcEmployeeLogTemplateFileName = "etl_tm.docx";
        String outputCertificationFileName = String.format("%s_%s_certi.pdf", userId, timestamp);
        String outputEmployeeLogPdfFileName = String.format("%s_%s_tm.pdf", userId, timestamp);
        String outputEmployeeLogDocxFileName = String.format("%s_%s_tm.docx", userId, timestamp);

        // Employee Training Log Full Path
//        InputStream is = CurriculumVitaeReportService.class.getResourceAsStream(srcEmployeeLogTemplateFileName);
//        String srcEmployeeLogFilePath = sourceRootFoloer + srcEmployeeLogTemplateFileName;
        InputStream logSource = CurriculumVitaeReportService.class.getResourceAsStream(srcEmployeeLogTemplateFileName);
        String outputEmployeeLogFilePath = outputPath + outputEmployeeLogPdfFileName;
        String outputEmployeeLogDocxFilePath = outputPath + outputEmployeeLogDocxFileName;

//        Path sourcePath = Paths.get(sourceRootFoloer, srcEmployeeLogTemplateFileName);
//        Path outputPath = Paths.get(outputRootFoloer, outputEmployeeLogFileName);

        // ????????? Full Path
        String outputCertificationFilePath = outputPath + outputCertificationFileName;
//        String outputLogFilePath = outputRootFoloer + outputEmployeeLogFileName;

        try {
            log.info("@userid : {} ????????? ?????? ?????? #1", userId);
            // Employee Training Log Data
            EmployeeTrainingLogSource trainingLogSource = binderLogService.getEmployeeTrainingLog(userId, timestamp);

//            log.info("trainingLogSource : {}", trainingLogSource);
            // Employee Training Log Data??? source??? ????????????.
            DataSourceInfo dataSourceInfo = new DataSourceInfo(trainingLogSource, null);

            DocumentAssembler assembler = new DocumentAssembler();
            assembler.getKnownTypes().add(EmployeeTrainingLogSource.class);
            assembler.getKnownTypes().add(CourseTrainingLog.class);
            assembler.getKnownTypes().add(Account.class);

//            assembler.setOptions(DocumentAssemblyOptions.UPDATE_FIELDS_AND_FORMULAS);
//            assembler.assembleDocument(in, out, new DataSourceInfo( accountList, "accountList"));

            // 2. ????????? ?????? ??????(????????????)
            log.info("==> Certi PDF ?????? ??????. {}", outputCertificationFilePath);
            String certHtmlContent = certificateFileService.createCertificateFileMerge(userId, timestamp, outputCertificationFilePath);

            // 1. Employee Training Log File ??????
//            log.info("sourcePath : {}", srcEmployeeLogFilePath);
//            log.info("outputPath : {}", outputEmployeeLogFilePath);

            assembler.setOptions(DocumentAssemblyOptions.INLINE_ERROR_MESSAGES);

            log.info("==> Word ????????? ?????????");
            log.info("outputEmployeeLogFilePath : {}", outputEmployeeLogFilePath);

            boolean result = assembler.assembleDocument(logSource, new FileOutputStream(outputEmployeeLogDocxFilePath), dataSourceInfo);
            log.info("<== ???????????? Word ?????? ??????[{}] : {}", outputEmployeeLogDocxFilePath, result);


//            output.flush();
            if(result) {
                String html = documentConverter.word2html(outputEmployeeLogDocxFilePath);
//                log.debug("Word to HTML : {}", html);

                if(ObjectUtils.isEmpty(trainingRecordId)) {
                    QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
                    BooleanBuilder builder = new BooleanBuilder();
                    builder.and(qTrainingRecord.status.eq(TrainingRecordStatus.PUBLISHED));
                    builder.and(qTrainingRecord.username.eq(userId));
                    Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findOne(builder);
                    if(optionalTrainingRecord.isPresent()) {
                        log.info("@Userid : {} ?????? ?????? ??? ?????? ?????? ????????? ?????????//?????? ????????? published ????????? ????????????, ?????? ???????????? ?????????.", userId);
                        trainingRecordId = optionalTrainingRecord.get().getId();
                        log.info("@Userid : {}, Training Record Id : {} ??? ???????????? ????????? ??????", userId, trainingRecordId);
                    }
                }
                saveTrainingRecord(trainingRecordId, userId, outputEmployeeLogPdfFileName, html, StringUtils.isEmpty(certHtmlContent) ? "" : outputCertificationFileName, certHtmlContent);

                //word to pdf
                assembler.assembleDocument(outputEmployeeLogDocxFilePath, outputEmployeeLogFilePath , dataSourceInfo);
            }

            log.info("<== ?????? ??????. ????????? ????????? : {}", userId);
            return true;
        } catch (Exception exp) {
            log.error("????????? : {}, ?????? ?????? ????????? ?????? ??? ??????: {}", userId, exp);
            return false;
        }

    }

    public void saveTrainingRecord(Integer trainingRecordId, String userId, String outputFilename, String tmHtml, String certFileName, String certHtml) {
        TrainingRecord trainingRecord;
        if(ObjectUtils.isEmpty(trainingRecordId)) {
            trainingRecord = new TrainingRecord();
            trainingRecord.setUsername(userId);
        } else {
            trainingRecord = trainingRecordRepository.findById(trainingRecordId).get();
        }
        trainingRecord.setStatus(TrainingRecordStatus.PUBLISHED);
        trainingRecord.setTmStatus(TrainingRecordStatus.PUBLISHED);
        trainingRecord.setTmPublishDate(new Date());
        trainingRecord.setTmFileName(outputFilename);
        trainingRecord.setTmHtmlContent(tmHtml);

        trainingRecord.setTmCertFileName(certFileName);
        trainingRecord.setTmCertHtmlContent(certHtml);

        trainingRecordRepository.save(trainingRecord);
    }
}
