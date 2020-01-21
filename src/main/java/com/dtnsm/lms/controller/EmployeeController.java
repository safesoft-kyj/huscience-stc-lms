package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.*;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.entity.constant.TrainingRecordStatus;
import com.dtnsm.common.repository.JobDescriptionVersionRepository;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.TrainingRecordRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.common.utils.Base64Utils;
import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.BinderAlarmType;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.domain.constant.TrainingRecordReviewStatus;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.JobDescriptionFileService;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.DocumentConverter;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
//import com.dtnsm.lms.xdocreport.JobDescriptionReportService;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.dtnsm.lms.xdocreport.JobDescriptionReportService;
import com.dtnsm.lms.xdocreport.dto.JobDescriptionSign;
import com.groupdocs.assembly.DataSourceInfo;
import com.joestelmach.natty.generated.b;
import com.querydsl.core.BooleanBuilder;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JobDescriptionVersionRepository jobDescriptionVersionRepository;
    private final UserJobDescriptionRepository userJobDescriptionRepository;
    private final SignatureRepository signatureRepository;
    private final JobDescriptionFileService jobDescriptionFileService;
    private final TrainingRecordReviewRepository trainingRecordReviewRepository;
    private final TrainingRecordRepository trainingRecordRepository;
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final JobDescriptionReportService jobDescriptionReportService;
    private final FileUploadProperties prop;
    private final MailService mailService;
    private final DocumentConverter documentConverter;
    private PageInfo pageInfo = new PageInfo();


    @GetMapping("/employees/review")
    public String getTrainingRecordReviewList(@RequestParam(value = "status", required = false, defaultValue = "REQUEST") TrainingRecordReviewStatus status, Model model) {
        pageInfo.setParentId("m-team-dept");
        pageInfo.setParentTitle("Team/Department");

        pageInfo.setPageId("m-emp");
        pageInfo.setPageTitle("바인더 검토");
        model.addAttribute(pageInfo);

        model.addAttribute("status", status);

        Optional<List<Account>> optionalAccounts = userService.findByParentUserId(SessionUtil.getUserId());
        if(optionalAccounts.isPresent()) {
            List<Account> accounts = optionalAccounts.get();
            List<String> userIds = accounts.stream().map(Account::getUserId).collect(Collectors.toList());

            QTrainingRecordReview qTrainingRecordReview = QTrainingRecordReview.trainingRecordReview;
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(qTrainingRecordReview.account.userId.in(userIds));
            builder.and(qTrainingRecordReview.status.eq(status));
            model.addAttribute("trainingRecord", trainingRecordReviewRepository.findAll(builder, qTrainingRecordReview.id.desc()));
        }
        return "content/employee/list";
    }

    @GetMapping("/employees/review/{id}")
    public String getTrainingRecordReview(@PathVariable("id") Integer id, Model model) {
        pageInfo.setParentId("m-team-dept");
        pageInfo.setParentTitle("Team/Department");

        pageInfo.setPageId("m-emp");
        pageInfo.setPageTitle("바인더 검토");
        model.addAttribute(pageInfo);

        model.addAttribute("trainingRecord", trainingRecordReviewRepository.findById(id).get());
        return "content/employee/review";
    }

    @GetMapping("/employees/cv/{status}")
    public String getEmployeeCV(@PathVariable("status") String stringStatus, @RequestParam(value = "id", required = false, defaultValue = "") String userId, Model model) {
        pageInfo.setParentId("m-employee");
        pageInfo.setParentTitle("Team/Department");

        pageInfo.setPageId("m-jd");
        pageInfo.setPageTitle("Curriculum Vitae");
        model.addAttribute(pageInfo);

        CurriculumVitaeStatus status = CurriculumVitaeStatus.valueOf(stringStatus.toUpperCase());


        Optional<List<Account>> optionalAccounts = userService.findByParentUserId(SessionUtil.getUserId());
        if(optionalAccounts.isPresent()) {
            model.addAttribute("users", optionalAccounts.get());
            model.addAttribute("userId", userId);
            model.addAttribute("cvList", userRepository.getUserCurriculumVitaeList(SessionUtil.getUserId(), status));
        }
        return "content/employee/cv/list";
    }

    @Deprecated
    @PostMapping("employees/cv/approval")
    public String approvalEmployeeCV(@RequestParam("id") Integer id) {
        Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findById(id);
        if(optionalCurriculumVitae.isPresent()) {

            CurriculumVitae cv = optionalCurriculumVitae.get();
            cv.setReviewedDate(new Date());
            cv.setReviewerId(SessionUtil.getUserId());
            cv.setStatus(CurriculumVitaeStatus.CURRENT);
            curriculumVitaeRepository.save(cv);

            //TODO CV (승인 알림) :: CV 단독 검토 기능 제거
        }

        return "redirect:/employees/cv/current";
    }

    @GetMapping("/employees/jd/{status}")
    public String getEmployeeJD(@PathVariable("status") String stringStatus, @RequestParam(value = "id", required = false, defaultValue = "") String userId, Model model) {
        pageInfo.setParentId("m-employee");
        pageInfo.setParentTitle("Team/Department");

        pageInfo.setPageId("m-jd");
        pageInfo.setPageTitle("직무 관리");
        model.addAttribute(pageInfo);

        JobDescriptionStatus status = JobDescriptionStatus.valueOf(stringStatus.toUpperCase());

        /**
         * CURRENT 상태의 직무 정보를 가져온다.
         */
        List<JobDescriptionVersion> jobDescriptionVersions = jobDescriptionVersionRepository.getCurrentJobDescriptionVersions();
        model.addAttribute("jdVersions", jobDescriptionVersions);

        Optional<List<Account>> optionalAccounts = userService.findByParentUserId(SessionUtil.getUserId());
        if(optionalAccounts.isPresent()) {
            model.addAttribute("users", optionalAccounts.get());
            model.addAttribute("userId", userId);
            model.addAttribute("userJobDescriptions", userRepository.getUserJobDescriptionByParentUserId(SessionUtil.getUserId(), status));
        }
        return "content/employee/jd/list";
    }

    @PostMapping("/employees/jd/revoke")
    public String revokeJd(@RequestParam(value = "id") Integer id, RedirectAttributes attributes) {

        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findById(id);
        if(optionalUserJobDescription.isPresent()) {
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setStatus(JobDescriptionStatus.SUPERSEDED);
            log.info("@UserJobDescription Id : {}, Superseded 상태로 변경(직무 배정 해제)", id);
            userJobDescriptionRepository.save(userJobDescription);
            attributes.addFlashAttribute("message", "직무 배정 해제 처리가 정상적으로 처리 되었습니다.");
        } else {
            attributes.addFlashAttribute("message", "데이터 조회에 실패 하였습니다.(사용자의 JD 정보 찾지 못함.)");

        }
        return "redirect:/employees/jd/approved";
    }



    @PostMapping("/employees/jd/remove")
    public String removeJd(@RequestParam(value = "id") Integer id, RedirectAttributes attributes) {

        userJobDescriptionRepository.deleteById(id);
        attributes.addFlashAttribute("message", "선택하신 사용자의 직무가 삭제 되었습니다.");
        return "redirect:/employees/jd/approved";
    }

    @PostMapping("/employees/review/{id}")
    @Transactional
    public String updateTrainingRecordReview(@PathVariable("id") Integer id) {
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        TrainingRecordReview trainingRecordReview = trainingRecordReviewRepository.findById(id).get();
        trainingRecordReview.setStatus(TrainingRecordReviewStatus.REVIEWED);
        trainingRecordReview.setDateOfReview(new Date());
        trainingRecordReview.setReviewerName(SessionUtil.getUserDetail().getUsername());
        trainingRecordReview.setSignature(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : null);

        TrainingRecordReview savedTrainingRecordReview = trainingRecordReviewRepository.save(trainingRecordReview);

        if(!ObjectUtils.isEmpty(trainingRecordReview.getCurriculumVitae())) {
            CurriculumVitae cv = trainingRecordReview.getCurriculumVitae();
            cv.setReviewed(true);
            curriculumVitaeRepository.save(cv);
        }

        if(!ObjectUtils.isEmpty(trainingRecordReview.getTrainingRecordReviewJdList())) {
            for(TrainingRecordReviewJd jd : trainingRecordReview.getTrainingRecordReviewJdList()) {
                UserJobDescription userJobDescription = jd.getUserJobDescription();
                userJobDescription.setReviewed(true);

                userJobDescriptionRepository.save(userJobDescription);
            }
        }

        if(!ObjectUtils.isEmpty(trainingRecordReview.getTrainingRecord())) {
            TrainingRecord trainingRecord = trainingRecordReview.getTrainingRecord();
//            if(!ObjectUtils.isEmpty(trainingRecord.getSopFileName())) {
            trainingRecord.setStatus(TrainingRecordStatus.REVIEWED);
//            }

            trainingRecordRepository.save(trainingRecord);
        }

        log.info("@@@ Digital Binder 생성 시작.");
        Account user = savedTrainingRecordReview.getAccount();
        Runnable r = () -> createBinderPDF(user, savedTrainingRecordReview);
        new Thread(r).start();


        //TODO Review 완료

        log.info("사용자에게 Binder 검토 완료 메일 전송 : {}", user.getEmail());
        Mail mail = new Mail();
        mail.setEmail(user.getEmail());
        mailService.send(mail, user.getName(), BinderAlarmType.BINDER_REVIEWED);
        return "redirect:/employees/review";
    }

    private void createBinderPDF(Account account, TrainingRecordReview trainingRecordReview) {
        try {
            String binderPath = prop.getBinderDir();
            String binderPdf = account.getUserId() + "_"+trainingRecordReview.getId()+".pdf";
            String coverPdf = account.getUserId() + "_cover.pdf";

            PDFMergerUtility mergerUtility = new PDFMergerUtility();
            // 출력파일(패스포함)
            mergerUtility.setDestinationFileName(binderPath + "/" + binderPdf);
            //cover
            InputStream is = CurriculumVitaeReportService.class.getResourceAsStream("cover.docx");
            DataSourceInfo dataSourceInfo = new DataSourceInfo(account, "");
            FileOutputStream os = new FileOutputStream(new File(binderPath + "/" + coverPdf));
            boolean created = documentConverter.assembleDocument(is, os, dataSourceInfo);
            log.debug("@Cover created : {}", created);
            if(created) {
                mergerUtility.addSource(binderPath + "/" + coverPdf);
            }

            //cv
            Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findTop1ByAccountAndStatusOrderByIdDesc(account, CurriculumVitaeStatus.CURRENT);
            if(optionalCurriculumVitae.isPresent()) {
                CurriculumVitae cv = optionalCurriculumVitae.get();
                String cvPath = binderPath + "/" + cv.getCvFileName();
                log.info("@cvPath : {}", cvPath);
                File file = new File(cvPath);
                if(file.exists()) {
                    mergerUtility.addSource(file);
                }
            }
            //jd
            Iterable<UserJobDescription> currentJdList = getJobDescriptionList(account.getUserId(), JobDescriptionStatus.APPROVED);
            currentJdList.forEach(jd -> {
                File f = new File(binderPath + "/" + jd.getJdFileName());
                if(f.exists()) {
                    try {
                        mergerUtility.addSource(f);
                    } catch (Exception e){}
                }
            });
            Iterable<UserJobDescription> supersededJdList = getJobDescriptionList(account.getUserId(), JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED);
            supersededJdList.forEach(jd -> {
                File f = new File(binderPath + "/" + jd.getJdFileName());
                if(f.exists()) {
                    try {
                        mergerUtility.addSource(f);
                    } catch (Exception e){}
                }
            });
            //trainingLog(SOP)
            Optional<TrainingRecord> optionalTrainingRecordSOP = getTrainingRecord(account.getUserId(), "sop");
            if(optionalTrainingRecordSOP.isPresent()) {
                String sopPath = binderPath + "/" + optionalTrainingRecordSOP.get().getSopFileName();
                log.info("@sopPath : {}", sopPath);
                File file = new File(sopPath);
                if(file.exists()) {
                    mergerUtility.addSource(file);
                }
            }
            //trainingLog(TM)
            Optional<TrainingRecord> optionalTrainingRecordTM = getTrainingRecord(account.getUserId(), "tm");
            if(optionalTrainingRecordTM.isPresent()) {
                String tmPath = binderPath + "/" + optionalTrainingRecordTM.get().getTmFileName();
                log.info("@tmPath : {}", tmPath);
                File file = new File(tmPath);
                if(file.exists()) {
                    mergerUtility.addSource(file);
                }
            }
            //certification
            Optional<TrainingRecord> optionalTrainingRecordCert = getTrainingRecord(account.getUserId(), "cert");
            if(optionalTrainingRecordCert.isPresent()) {
                String certPath = binderPath + "/" + optionalTrainingRecordCert.get().getTmCertFileName();
                log.info("@certPath : {}", certPath);
                File file = new File(certPath);
                if(file.exists()) {
                    mergerUtility.addSource(file);
                }
            }

            MemoryUsageSetting setupMainMemoryOnly = MemoryUsageSetting.setupMainMemoryOnly();
            mergerUtility.mergeDocuments(setupMainMemoryOnly);

            trainingRecordReview.setBinderPdf(binderPdf);
            trainingRecordReviewRepository.save(trainingRecordReview);
        } catch (Exception error) {
            log.error("error : {}", error);
        }
    }

    private Optional<TrainingRecord> getTrainingRecord(String userId, String record) {
        BooleanBuilder builder = new BooleanBuilder();
        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        builder.and(qTrainingRecord.username.eq(userId));
        if("sop".equals(record)) {
            builder.and(qTrainingRecord.sopFileName.isNotEmpty());
        } else if("tm".equals(record)) {
            builder.and(qTrainingRecord.tmFileName.isNotEmpty());
        } else if("cert".equals(record)) {
            builder.and(qTrainingRecord.tmCertFileName.isNotEmpty());
        }
        builder.and(qTrainingRecord.status.eq(TrainingRecordStatus.REVIEWED));
        Iterable<TrainingRecord> trainingRecords = trainingRecordRepository.findAll(builder);
        if(ObjectUtils.isEmpty(trainingRecords)) {
            return Optional.empty();
        } else {
            return Optional.of(trainingRecords.iterator().next());
        }
    }

    private Iterable<UserJobDescription> getJobDescriptionList(String userId, JobDescriptionStatus ... statuses) {
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserJobDescription.status.in(statuses));
        builder.and(qUserJobDescription.username.eq(userId));
//        builder.and(qUserJobDescription.reviewed.eq(false));

        return userJobDescriptionRepository.findAll(builder, qUserJobDescription.id.desc());
    }

    @PostMapping("/employees/jd")
    public String assignEmployeeJD(UserJobDescription userJobDescription) {
        Account account = userRepository.findByUserId(userJobDescription.getUsername());
        userJobDescription.setEmployeeName(account.getEngName());
        userJobDescription.setStatus(JobDescriptionStatus.ASSIGNED);
        userJobDescriptionRepository.save(userJobDescription);

        //TODO 알림 전송(JD 배정 알림)
        String toEmail = account.getEmail();
        log.info("사용자에게 JD 배정 알림 메일 전송 : {}", toEmail);
        Mail mail = new Mail();
        mail.setEmail(toEmail);
        mailService.send(mail, account.getName(), BinderAlarmType.JD_ASSIGNED);
        return "redirect:/employees/jd/approved";
    }

    private String getManagerJobTitle(String userId) {
        BooleanBuilder builder = new BooleanBuilder();
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        builder.and(qUserJobDescription.username.eq(userId));
        builder.and(qUserJobDescription.status.eq(JobDescriptionStatus.APPROVED));
        Iterable<UserJobDescription> userJobDescriptions = userJobDescriptionRepository.findAll(builder);
        if(ObjectUtils.isEmpty(userJobDescriptions)) {
            log.error("매니저 Job Description 정보가 없습니다. {}", userId);
            return "N/A";
        } else {
            return StreamSupport.stream(userJobDescriptions.spliterator(), false)
                    .map(u -> u.getJobDescriptionVersion().getJobDescription().getTitle())
                    .collect(Collectors.joining(","));
        }
    }

    @PostMapping("employees/jd/approval")
    public String approvalEmployeeJD(@RequestParam("id") Integer id) {
        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findById(id);
        if(optionalUserJobDescription.isPresent()) {
            Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setApprovedDate(new Date());
            userJobDescription.setStatus(JobDescriptionStatus.APPROVED);
            userJobDescription.setApprovedSign(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "");
            userJobDescription.setManagerName(SessionUtil.getUserDetail().getUser().getEngName());
            userJobDescription.setManagerTitle(getManagerJobTitle(SessionUtil.getUserId()));

            /**
             *  같은 JD의 이전 버전을 Superseded 상태로 변경한다.
             */
            superseded(userJobDescription.getUsername(), userJobDescription.getJobDescriptionVersion().getJobDescription().getId());
            userJobDescriptionRepository.save(userJobDescription);

            /**
             * 직원의 서명이 들어간 JD 파일(PDF) 생성 및 HTML 변환 정보 반환
             */
            new Thread(() -> {
                try {
                    JobDescriptionVersionFile file = userJobDescription.getJobDescriptionVersion().getJobDescriptionVersionFile();
                    Resource resource = jobDescriptionFileService.loadFileAsResource(file.getSaveName());
                    String format = "dd-MMM-yyyy";
                    JobDescriptionSign jobDescriptionSign = JobDescriptionSign.builder()
                            .assignDate(DateUtil.getDateToString(userJobDescription.getAssignDate(), format).toUpperCase())
                            .agreeDate(ObjectUtils.isEmpty(userJobDescription.getAgreeDate()) ? "" : DateUtil.getDateToString(userJobDescription.getAgreeDate(), format).toUpperCase())
                            .employeeName(userJobDescription.getEmployeeName())
                            .approvedDate(ObjectUtils.isEmpty(userJobDescription.getApprovedDate()) ? "" : DateUtil.getDateToString(userJobDescription.getApprovedDate(), format).toUpperCase())
                            .managerName(userJobDescription.getManagerName())
                            .managerTitle(userJobDescription.getManagerTitle())
                            .empSignStr(userJobDescription.getAgreeSign())
                            .mngSignStr(userJobDescription.getApprovedSign())
                            .empSign(StringUtils.isEmpty(userJobDescription.getAgreeSign()) ? null : new ByteArrayImageProvider(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(userJobDescription.getAgreeSign()))))
                            .mngSign(StringUtils.isEmpty(userJobDescription.getApprovedSign()) ? null : new ByteArrayImageProvider(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(userJobDescription.getApprovedSign())))).build();

//                    String pdfOutput = prop.getBinderJdUploadDir() + "/JD_" + userJobDescription.getId() + ".pdf";

                    jobDescriptionReportService.generateReport(jobDescriptionSign, resource.getInputStream(), id);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }).run();

            //TODO Job Description (승인 알림)
            Account account = userRepository.findByUserId(userJobDescription.getUsername());
            String toEmail = account.getEmail();
            log.info("사용자에게 JD 승인 알림 메일 전송 : {}", toEmail);
            Mail mail = new Mail();
            mail.setEmail(toEmail);
            mailService.send(mail, account.getName(), BinderAlarmType.JD_APPROVED);

        }

        return "redirect:/employees/jd/approved";
    }

    private void superseded(String username, Integer jobDescriptionId) {
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserJobDescription.username.eq(username));
        builder.and(qUserJobDescription.jobDescriptionVersion.jobDescription.id.eq(jobDescriptionId));
        builder.and(qUserJobDescription.status.eq(JobDescriptionStatus.APPROVED));
        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findOne(builder);
        if(optionalUserJobDescription.isPresent()) {
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setStatus(JobDescriptionStatus.SUPERSEDED);

            userJobDescriptionRepository.save(userJobDescription);
        }
    }
}
