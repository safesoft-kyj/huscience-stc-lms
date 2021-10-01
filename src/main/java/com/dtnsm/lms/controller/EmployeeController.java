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
import com.dtnsm.lms.domain.DTO.ReviewHist;
import com.dtnsm.lms.domain.DTO.ReviewHistDTO;
import com.dtnsm.lms.domain.constant.BinderAlarmType;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.domain.constant.TrainingRecordReviewStatus;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewJdRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.CourseManagerService;
import com.dtnsm.lms.service.JobDescriptionFileService;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.util.*;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.dtnsm.lms.xdocreport.JobDescriptionReportService;
import com.dtnsm.lms.xdocreport.dto.JobDescriptionSign;
import com.groupdocs.assembly.DataSourceInfo;
import com.groupdocs.merger.Merger;
import com.groupdocs.merger.domain.FileType;
import com.groupdocs.merger.domain.options.LoadOptions;
import com.querydsl.core.BooleanBuilder;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.Base;
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
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//import com.dtnsm.lms.xdocreport.JobDescriptionReportService;

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
    private final TrainingRecordReviewJdRepository trainingRecordReviewJdRepository;
    private final TrainingRecordReviewRepository trainingRecordReviewRepository;
    private final TrainingRecordRepository trainingRecordRepository;
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final JobDescriptionReportService jobDescriptionReportService;
    private final FileUploadProperties prop;
    private final MailService mailService;
    private final DocumentConverter documentConverter;
    private final CourseManagerService courseManagerService;

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

//    @Deprecated
//    @PostMapping("employees/cv/approval")
//    public String approvalEmployeeCV(@RequestParam("id") Integer id) {
//        Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findById(id);
//        if(optionalCurriculumVitae.isPresent()) {
//
//            CurriculumVitae cv = optionalCurriculumVitae.get();
//            cv.setReviewedDate(new Date());
//            cv.setReviewerId(SessionUtil.getUserId());
//            cv.setStatus(CurriculumVitaeStatus.CURRENT);
//            curriculumVitaeRepository.save(cv);
//
//            //TODO CV (승인 알림) :: CV 단독 검토 기능 제거
//        }
//
//        return "redirect:/employees/cv/current";
//    }

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

        //Optional<List<Account>> optionalAccounts = userService.findByParentUserId(SessionUtil.getUserId());
        Optional<List<Account>> optionalAccounts = userService.findAllByParentUserIdAndEnabled(SessionUtil.getUserId(), true);

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

            log.info("@Remoke Job Description : {}", userJobDescription);

//            log.info("@UserJobDescription Id : {}, Superseded 상태로 변경(직무 배정 해제)", id);
            userJobDescriptionRepository.save(userJobDescription);
            attributes.addFlashAttribute("type", "success");
            attributes.addFlashAttribute("msg", "직무 배정 해제 처리가 정상적으로 처리 되었습니다.");
        } else {
            attributes.addFlashAttribute("type", "error");
            attributes.addFlashAttribute("msg", "데이터 조회에 실패 하였습니다.(사용자의 JD 정보 찾지 못함.)");
        }
        return "redirect:/employees/jd/approved";
    }



    @PostMapping("/employees/jd/remove")
    public String removeJd(@RequestParam(value = "id") Integer id, RedirectAttributes attributes) {

        userJobDescriptionRepository.deleteById(id);
        attributes.addFlashAttribute("type", "info");
        attributes.addFlashAttribute("msg", "선택하신 사용자의 직무가 삭제 되었습니다.");
        return "redirect:/employees/jd/approved";
    }

    @PostMapping("/employees/review/{id}")
    @Transactional
    public String updateTrainingRecordReview(@PathVariable("id") Integer id,
                                             @RequestParam("status") TrainingRecordReviewStatus status,
                                             @RequestParam(value = "reason", required = false) String reason) {
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        TrainingRecordReview trainingRecordReview = trainingRecordReviewRepository.findById(id).get();
        trainingRecordReview.setStatus(status);
        trainingRecordReview.setDateOfReview(new Date());
        trainingRecordReview.setReviewerName(SessionUtil.getUserDetail().getEngName());
        trainingRecordReview.setReason(reason);
        trainingRecordReview.setSignature(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : null);

        TrainingRecordReview savedTrainingRecordReview = trainingRecordReviewRepository.save(trainingRecordReview);
        Account user = savedTrainingRecordReview.getAccount();

        // CV
        if(!ObjectUtils.isEmpty(trainingRecordReview.getCurriculumVitae())) {
            CurriculumVitae cv = trainingRecordReview.getCurriculumVitae();
            if(status == TrainingRecordReviewStatus.REVIEWED){
                cv.setReviewed(true);
                cv.setReviewedDate(new Date());
            } else{
                cv.setReviewed(false);
            }
            curriculumVitaeRepository.save(cv);
        }

        // TR
        if(!ObjectUtils.isEmpty(trainingRecordReview.getTrainingRecord())) {
            TrainingRecord trainingRecord = trainingRecordReview.getTrainingRecord();
            if(status == TrainingRecordReviewStatus.REVIEWED) {
                trainingRecord.setStatus(TrainingRecordStatus.REVIEWED);
            } else {
                //trainingRecord.setStatus(TrainingRecordStatus.REJECTED);
                trainingRecord.setStatus(TrainingRecordStatus.PUBLISHED);
            }

            trainingRecordRepository.save(trainingRecord);
        }

        // JD
        if(status == TrainingRecordReviewStatus.REVIEWED){
            // TrainingRecordReviewJd 생성
            Iterable<UserJobDescription> jobDescriptions = getJobDescriptionList(user.getUserId(), JobDescriptionStatus.APPROVED);
            if(!ObjectUtils.isEmpty(jobDescriptions)) {

                List<UserJobDescription> jobDescriptionList = StreamSupport.stream(jobDescriptions.spliterator(), false)
                        .filter(jd -> !jd.isReviewed())
                        .collect(Collectors.toList());

                for(UserJobDescription userJobDescription : jobDescriptionList) {
                    Optional<TrainingRecordReviewJd> optionalTrainingRecordReviewJd = getTrainingRecordReviewJd(userJobDescription.getId());
                    if(optionalTrainingRecordReviewJd.isPresent() == false) {
                        TrainingRecordReviewJd trainingRecordReviewJd = new TrainingRecordReviewJd();
                        trainingRecordReviewJd.setTrainingRecordReview(savedTrainingRecordReview);
                        trainingRecordReviewJd.setUserJobDescription(userJobDescription);
                        trainingRecordReviewJdRepository.save(trainingRecordReviewJd);
                    }
                }

            }
            
            // UserJobDescription 상태 변경
            for(TrainingRecordReviewJd jd : trainingRecordReview.getTrainingRecordReviewJdList()) {
                UserJobDescription userJobDescription = jd.getUserJobDescription();
                userJobDescription.setReviewed(true);
                userJobDescriptionRepository.save(userJobDescription);
            }
        }

//        log.info("사용자에게 Binder 검토 : {}, 메일 전송 : {}", status.name(), user.getEmail());
        Context context = new Context();
        context.setVariable("empName", user.getName());
        if(status == TrainingRecordReviewStatus.REVIEWED) {
            mailService.send(user.getEmail(), String.format(BinderAlarmType.BINDER_REVIEWED.getTitle(), user.getName()), BinderAlarmType.BINDER_REVIEWED, context);

            new Thread(() -> {
//                log.info("@@@ Digital Binder 생성 시작.");
                createBinderPDF(user, savedTrainingRecordReview);
            }).start();
        } else {
            context.setVariable("reason", reason);
            mailService.send(user.getEmail(), String.format(BinderAlarmType.BINDER_REJECT.getTitle(), user.getName()), BinderAlarmType.BINDER_REJECT, context);
        }

        return "redirect:/employees/review";
    }

    private Merger createOrJoin(Merger merger, InputStream is) throws Exception {
        if(merger == null) {
            LoadOptions loadOptions = new LoadOptions(FileType.PDF);
            loadOptions.setEncoding(Charset.forName("UTF-8"));
            merger = new Merger(is, loadOptions);
        } else {
            merger.join(is);
        }

        return merger;
    }

    private ReviewHistDTO getReviewHistory(Account account) {
//        List<TrainingRecordReview> reviews = trainingRecordReviewRepository.findAllByAccountAndStatusOrderByDateOfReviewDesc(account, TrainingRecordReviewStatus.REVIEWED);
        List<TrainingRecordReview> reviews = trainingRecordReviewRepository.findAllByAccountAndStatusOrderByDateOfReviewAsc(account, TrainingRecordReviewStatus.REVIEWED);

        List<ReviewHist> reviewHists = new ArrayList<>();
        boolean init = true;
        for(TrainingRecordReview review : reviews) {
            ReviewHist hist = new ReviewHist();
            hist.setDateOfReview(DateUtil.getDateToString(review.getDateOfReview(), "dd-MMM-yyyy").toUpperCase());
            hist.setRevName(review.getReviewerName());

            boolean isJobD = trainingRecordReviewJdRepository.findAllByTrainingRecordReview(review).isPresent();
            hist.setCvLabel((ObjectUtils.isEmpty(review.getCurriculumVitae()) ? "□" : "■") + " CV (" + (init ? "initial" : "Update") +")");
            hist.setJdLabel((isJobD == false ? "□" : "■") + " JD (" + (init ? "initial" : "Update") +")");
            hist.setTrLabel((ObjectUtils.isEmpty(review.getTrainingRecord()) ? "□" : "■") + " Employee Training Log");

            hist.setRevSign(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(review.getSignature())));
            hist.setEmpSign(new ByteArrayInputStream(Base64Utils.decodeBase64ToBytes(signatureRepository.findById(account.getUserId()).get().getBase64signature())));
            reviewHists.add(hist);

            init = false;
        }

        ReviewHistDTO reviewHistDTO = new ReviewHistDTO();
        reviewHistDTO.setDisplayName(account.getEngName());
        reviewHistDTO.setReviewHistList(reviewHists);
        reviewHistDTO.setDateStarted(DateUtil.getDateToString(DateUtil.getStringToDate(account.getIndate(), "yyyy-MM-dd"), "dd-MMM-yyyy").toUpperCase());
        reviewHistDTO.setEmployeeNo(account.getComNum());
        reviewHistDTO.setDeptTeam(StringUtils.isEmpty(account.getOrgDepart()) ? (StringUtils.isEmpty(account.getOrgTeam()) ? "" : account.getOrgTeam())
                : account.getOrgDepart() + (StringUtils.isEmpty(account.getOrgTeam()) ? "" : "/" + account.getOrgTeam()) );

        String jobTitle = StreamSupport.stream(GlobalUtil.getJobDescriptionList(account.getUserId(), Arrays.asList(JobDescriptionStatus.APPROVED))
                .spliterator(), false)
                .map(u -> {
                    JobDescription jd = u.getJobDescriptionVersion().getJobDescription();
                    return jd.getTitle() + "(" + jd.getShortName() + ")";
                })
                .collect(Collectors.joining(","));
        reviewHistDTO.setJobTitle(jobTitle);

        return reviewHistDTO;

    }

    private void createBinderPDF(Account account, TrainingRecordReview trainingRecordReview) {
        try {
            String binderPath = prop.getBinderDir();
            String binderPdf = account.getUserId() + "_db_"+trainingRecordReview.getId()+".pdf";
            String coverDocx = account.getUserId() + "_cover_"+trainingRecordReview.getId()+".docx";
            String coverPdf = account.getUserId() + "_cover_"+trainingRecordReview.getId()+".pdf";
            String revHistDocx = account.getUserId() + "_tr_rev_hist_"+trainingRecordReview.getId()+".docx";
            String revHistPdf = account.getUserId() + "_tr_rev_hist_"+trainingRecordReview.getId()+".pdf";

            Merger merger = null;

            LoadOptions loadOptions = new LoadOptions(FileType.PDF);
            loadOptions.setEncoding(Charset.forName("UTF-8"));

            //cover
            InputStream is = CurriculumVitaeReportService.class.getResourceAsStream("cover.docx");
            DataSourceInfo dataSourceInfo = new DataSourceInfo(account, "");
            FileOutputStream os = new FileOutputStream(new File(binderPath + coverDocx));
            boolean created = documentConverter.assembleDocument(is, os, dataSourceInfo);
//            log.info("@Cover created : {}", created);
            if(created) {
                documentConverter.word2pdf(new FileInputStream(binderPath + coverDocx), new FileOutputStream(binderPath + coverPdf));
//                log.info("@cover docx2pdf 변환");
                 merger = new Merger(new FileInputStream(binderPath + coverPdf), loadOptions);
//                 log.info("@merger 객체 생성 {}{}", binderPath, coverPdf);
            }

            //reviewHistory
            InputStream trReviewHistIs = CurriculumVitaeReportService.class.getResourceAsStream("tr_review_hist.docx");
            DataSourceInfo trDataSource = new DataSourceInfo(getReviewHistory(account), "");
            FileOutputStream revOs = new FileOutputStream(new File(binderPath + revHistDocx));
            created = documentConverter.assembleDocument(trReviewHistIs, revOs, trDataSource);
//            log.info("@Cover created : {}", created);
            if(created) {
                log.info("@TR Review History 생성");
                documentConverter.word2pdf(new FileInputStream(binderPath + revHistDocx), new FileOutputStream(binderPath + revHistPdf));
                merger = createOrJoin(merger, new FileInputStream(binderPath + revHistPdf));
            }

            //cv
            Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findTop1ByAccountAndStatusOrderByIdDesc(account, CurriculumVitaeStatus.CURRENT);
            if(optionalCurriculumVitae.isPresent()) {
                CurriculumVitae cv = optionalCurriculumVitae.get();
                String cvPath = binderPath + cv.getCvFileName();
//                log.info("@cvPath : {}", cvPath);
                File file = new File(cvPath);
                if(file.exists()) {
                    merger = createOrJoin(merger, new FileInputStream(file));
//                    merger.join(new FileInputStream(file));
                }
            }
            //jd
//            log.info("@JD 확인");
            Iterable<UserJobDescription> currentJdList = getJobDescriptionList(account.getUserId(), JobDescriptionStatus.APPROVED);
            for(UserJobDescription jd : currentJdList) {
//                log.info("@add current Jd {}", jd.getJdFileName());
                File f = new File(binderPath + jd.getJdFileName());
                if(f.exists()) {
                    merger = createOrJoin(merger, new FileInputStream(f));
                }
            }
            Iterable<UserJobDescription> supersededJdList = getJobDescriptionList(account.getUserId(), JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED);
            for(UserJobDescription jd : supersededJdList) {
//                log.info("@add superseded Jd : {}", jd.getJdFileName());
                File f = new File(binderPath + jd.getJdFileName());
                if(f.exists()) {
                    merger = createOrJoin(merger, new FileInputStream(f));
                }
            }
            //trainingLog(SOP)
//            log.info("@trainingLog(SOP) 확인");
            Optional<TrainingRecord> optionalTrainingRecordSOP = getTrainingRecord(account.getUserId(), "sop");
            if(optionalTrainingRecordSOP.isPresent()) {
                String sopPath = binderPath + optionalTrainingRecordSOP.get().getSopFileName();
//                log.info("@sopPath : {}", sopPath);
                File file = new File(sopPath);
                if(file.exists()) {
                    ByteArrayOutputStream sop = new ByteArrayOutputStream();
                    documentConverter.word2pdf(new FileInputStream(file), sop);
                    merger = createOrJoin(merger, new ByteArrayInputStream(sop.toByteArray()));
                }
            }
            //trainingLog(TM)
//            log.info("@trainingLog(TM) 확인");
            Optional<TrainingRecord> optionalTrainingRecordTM = getTrainingRecord(account.getUserId(), "tm");
            if(optionalTrainingRecordTM.isPresent()) {
                String tmPath = binderPath + optionalTrainingRecordTM.get().getTmFileName();
//                log.info("@tmPath : {}", tmPath);
                File file = new File(tmPath);
                if(file.exists()) {
                    merger = createOrJoin(merger, new FileInputStream(file));
                }
            }
//            //certification
            Optional<TrainingRecord> optionalTrainingRecordCert = getTrainingRecord(account.getUserId(), "cert");
            if(optionalTrainingRecordCert.isPresent()) {
                String certPath = binderPath + optionalTrainingRecordCert.get().getTmCertFileName();
//                log.info("@certPath : {}", certPath);
                File file = new File(certPath);
                if(file.exists()) {
                    merger = createOrJoin(merger, new FileInputStream(file));
                }
            }

            FileOutputStream binder = new FileOutputStream(new File(binderPath + binderPdf));
            merger.save(binder);
            binder.flush();
            binder.close();
//            log.info("@@ Binder PDF Merge save....{}{}", binderPath, binderPdf);

            trainingRecordReview.setBinderPdf(binderPdf);
            trainingRecordReviewRepository.save(trainingRecordReview);

            Optional<Signature> optionalSignature = signatureRepository.findById(account.getUserId());
            if(optionalSignature.isPresent()) {
                Signature signature = optionalSignature.get();
                signature.setBinderFileName(binderPdf);
                signatureRepository.save(signature);
//                log.info("@eSOP에서 바인더 정보 조회 가능하도록 정보 업데이트(Signature)");
            }
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
        Iterable<TrainingRecord> trainingRecords = trainingRecordRepository.findAll(builder, QTrainingRecord.trainingRecord.id.desc());
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

    private Optional<TrainingRecordReviewJd> getTrainingRecordReviewJd(Integer userJobDescriptionId){
        QTrainingRecordReviewJd qTrainingRecordReviewJd = QTrainingRecordReviewJd.trainingRecordReviewJd;
        BooleanBuilder jdBuilder = new BooleanBuilder();
        jdBuilder.and(qTrainingRecordReviewJd.userJobDescription.id.eq(userJobDescriptionId));
        return trainingRecordReviewJdRepository.findOne(jdBuilder);
    }

    @PostMapping("/employees/jd")
    public String assignEmployeeJD(UserJobDescription userJobDescription, RedirectAttributes attributes) {

        // 211001 KJH 교육관리자가 없으면 JD를 부여할 수 없음
        if(courseManagerService.getCourseManager() == null){
            attributes.addFlashAttribute("type", "error");
            attributes.addFlashAttribute("msg", "교육관리자를 먼저 배정해주세요");
            return "redirect:/employees/jd/approved";
        }

        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserJobDescription.username.eq(userJobDescription.getUsername()));
        builder.and(qUserJobDescription.jobDescriptionVersion.id.eq(userJobDescription.getJobDescriptionVersion().getId()));
        builder.and(qUserJobDescription.status.eq(JobDescriptionStatus.ASSIGNED).or(qUserJobDescription.status.eq(JobDescriptionStatus.AGREE).or(qUserJobDescription.status.eq(JobDescriptionStatus.APPROVED))));

        if(!userJobDescriptionRepository.findOne(builder).isPresent()) {
            Account account = userRepository.findByUserId(userJobDescription.getUsername());
            userJobDescription.setEmployeeName(account.getEngName());
            userJobDescription.setStatus(JobDescriptionStatus.ASSIGNED);
            userJobDescriptionRepository.save(userJobDescription);

            //TODO 알림 전송(JD 배정 알림)
            String toEmail = account.getEmail();
//            log.info("사용자에게 JD 배정 알림 메일 전송 : {}", toEmail);
            Context context = new Context();
            String jobTitle = jobDescriptionVersionRepository.findById(userJobDescription.getJobDescriptionVersion().getId()).get().getJobDescription().getTitle();
            context.setVariable("jobTitle", jobTitle);
            mailService.send(toEmail, String.format(BinderAlarmType.JD_ASSIGNED.getTitle(), jobTitle, account.getName()), BinderAlarmType.JD_ASSIGNED, context);
            return "redirect:/employees/jd/approved";
        } else {
            attributes.addFlashAttribute("type", "warning");
            attributes.addFlashAttribute("msg", "이미 ASSIGNED/AGREE/APPROVED 중에 배정된 이력이 존재 합니다.");
            return "redirect:/employees/jd/approved";
        }
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
    public String approvalEmployeeJD(@RequestParam("id") Integer id, RedirectAttributes attributes) {

        // 211001 KJH 교육관리자가 없으면 JD를 부여할 수 없음
        if(courseManagerService.getCourseManager() == null){
            attributes.addFlashAttribute("type", "error");
            attributes.addFlashAttribute("msg", "교육관리자를 먼저 배정해주세요");
            return "redirect:/employees/jd/approved";
        }

        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findById(id);
        log.info("==> @user jd : {} approval.", id);
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
            superseded(userJobDescription.getUsername(), userJobDescription.getJobDescriptionVersion());
            log.info("@userJobDescription save. id : {}", id);
            userJobDescriptionRepository.save(userJobDescription);

            /**
             * 직원의 서명이 들어간 JD 파일(PDF) 생성 및 HTML 변환 정보 반환
             */
            new Thread(() -> {
                try {
                    log.info("==> user jobDescriptionVersionFile  jd id : {}", id);
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
                    log.info("@generateReport => jd report.");
                    jobDescriptionReportService.generateReport(jobDescriptionSign, resource.getInputStream(), id);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }).run();

            //TODO Job Description (승인 알림)
            Account account = userRepository.findByUserId(userJobDescription.getUsername());
            String toEmail = account.getEmail();
//            log.info("사용자에게 JD 승인 알림 메일 전송 : {}", toEmail);
//            Mail mail = new Mail();
//            mail.setEmail(toEmail);
            String jobTitle = userJobDescription.getJobDescriptionVersion().getJobDescription().getTitle();
            Context context = new Context();
            context.setVariable("empName", account.getName());
            context.setVariable("jobTitle", jobTitle);
            mailService.send(toEmail, String.format(BinderAlarmType.JD_APPROVED.getTitle(), jobTitle, account.getName()), BinderAlarmType.JD_APPROVED, context);

            // 관리자에게 메일 전송
            Account manager = userRepository.findByUserId(courseManagerService.getCourseManager().getUserId());
            String managerMail = manager.getEmail();
            mailService.send(managerMail, String.format(BinderAlarmType.JD_COMPLETED.getTitle(), jobTitle, account.getName()), BinderAlarmType.JD_COMPLETED, context);

        }

        return "redirect:/employees/jd/approved";
    }

    private void superseded(String username, JobDescriptionVersion jobDescriptionVersion) {
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserJobDescription.username.eq(username));
        builder.and(qUserJobDescription.jobDescriptionVersion.jobDescription.id.eq(jobDescriptionVersion.getJobDescription().getId()));
        builder.and(qUserJobDescription.jobDescriptionVersion.id.ne(jobDescriptionVersion.getId()));
        builder.and(qUserJobDescription.status.eq(JobDescriptionStatus.APPROVED));

        Optional<UserJobDescription> optionalUserJobDescription = userJobDescriptionRepository.findOne(builder);
        if(optionalUserJobDescription.isPresent()) {
            UserJobDescription userJobDescription = optionalUserJobDescription.get();
            userJobDescription.setStatus(JobDescriptionStatus.SUPERSEDED);

            userJobDescriptionRepository.save(userJobDescription);
        }
    }
}
