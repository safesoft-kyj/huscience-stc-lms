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
//        CommonUtilities.applyLicense();

        // 파일 기본 경로
//        String sourceRootFoloer = prop.getXdocUploadDir() + "Data//Storage//";
        String outputPath = prop.getBinderDir();



        String userId = SessionUtil.getUserId();
        long timestamp = System.currentTimeMillis();

        log.info("@userid : {} 트레이닝 이력 바인더 배포 시작", userId);
        // Employee Training Log Template 파일 및 출력 파일 지정
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

        // 수료증 Full Path
        String outputCertificationFilePath = outputPath + outputCertificationFileName;
//        String outputLogFilePath = outputRootFoloer + outputEmployeeLogFileName;

        try {
            log.info("@userid : {} 바인더 로그 변환 #1", userId);
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
                String html = documentConverter.word2html(outputEmployeeLogDocxFilePath);
                log.debug("Word to HTML : {}", html);

                if(ObjectUtils.isEmpty(trainingRecordId)) {
                    QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
                    BooleanBuilder builder = new BooleanBuilder();
                    builder.and(qTrainingRecord.status.eq(TrainingRecordStatus.PUBLISHED));
                    Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findOne(builder);
                    if(optionalTrainingRecord.isPresent()) {
                        log.info("@Userid : {} 배포 진행 중 신규 배포 이력이 존재함//배포 시점엔 published 상태가 없었으나, 중복 클릭으로 발생함.", userId);
                        trainingRecordId = optionalTrainingRecord.get().getId();
                        log.info("@Userid : {}, Training Record Id : {} 로 업데이트 되도록 변경", userId, trainingRecordId);
                    }
                }
                saveTrainingRecord(trainingRecordId, userId, outputEmployeeLogPdfFileName, html, StringUtils.isEmpty(certHtmlContent) ? "" : outputCertificationFileName, certHtmlContent);

                //word to pdf
                assembler.assembleDocument(outputEmployeeLogDocxFilePath, outputEmployeeLogFilePath , dataSourceInfo);
            }

            log.info("<== 배포 완료. 사용자 아이디 : {}", userId);
            return true;
        } catch (Exception exp) {
            log.error("사용자 : {}, 교육 이력 바인더 변환 중 오류: {}", userId, exp);
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
