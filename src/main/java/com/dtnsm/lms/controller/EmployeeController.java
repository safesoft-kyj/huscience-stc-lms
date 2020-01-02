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
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.domain.constant.TrainingRecordReviewStatus;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.JobDescriptionFileService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
//import com.dtnsm.lms.xdocreport.JobDescriptionReportService;
import com.dtnsm.lms.xdocreport.JobDescriptionReportService;
import com.dtnsm.lms.xdocreport.dto.JobDescriptionSign;
import com.joestelmach.natty.generated.b;
import com.querydsl.core.BooleanBuilder;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import lombok.RequiredArgsConstructor;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
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
    private PageInfo pageInfo = new PageInfo();


    @GetMapping("/employees/review")
    public String getTrainingRecordReviewList(@RequestParam(value = "status", required = false, defaultValue = "REQUEST") TrainingRecordReviewStatus status, Model model) {
        pageInfo.setParentId("m-team-dept");
        pageInfo.setParentTitle("Team/Department");

        pageInfo.setPageId("m-emp");
        pageInfo.setPageTitle("Training Record Review");
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
        pageInfo.setPageTitle("Training Record Review");
        model.addAttribute(pageInfo);

        model.addAttribute("trainingRecord", trainingRecordReviewRepository.findById(id).get());
        return "content/employee/review";
    }

    @GetMapping("/employees/cv/{status}")
    public String getEmployeeCV(@PathVariable("status") String stringStatus, @RequestParam(value = "id", required = false, defaultValue = "") String userId, Model model) {
        pageInfo.setParentId("m-employee");
        pageInfo.setParentTitle("Team/Dept Employees");

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

    @PostMapping("employees/cv/approval")
    public String approvalEmployeeCV(@RequestParam("id") Integer id) {
        Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findById(id);
        if(optionalCurriculumVitae.isPresent()) {

            CurriculumVitae cv = optionalCurriculumVitae.get();
            cv.setReviewedDate(new Date());
            cv.setReviewerId(SessionUtil.getUserId());
            cv.setStatus(CurriculumVitaeStatus.CURRENT);
            curriculumVitaeRepository.save(cv);

            //TODO CV (승인 알림)
        }

        return "redirect:/employees/cv/current";
    }

    @GetMapping("/employees/jd/{status}")
    public String getEmployeeJD(@PathVariable("status") String stringStatus, @RequestParam(value = "id", required = false, defaultValue = "") String userId, Model model) {
        pageInfo.setParentId("m-employee");
        pageInfo.setParentTitle("Team/Dept Employees");

        pageInfo.setPageId("m-jd");
        pageInfo.setPageTitle("Job Description");
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

    @PostMapping("/employees/review/{id}")
    @Transactional
    public String updateTrainingRecordReview(@PathVariable("id") Integer id) {
        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
        TrainingRecordReview trainingRecordReview = trainingRecordReviewRepository.findById(id).get();
        trainingRecordReview.setStatus(TrainingRecordReviewStatus.REVIEWED);
        trainingRecordReview.setDateOfReview(new Date());
        trainingRecordReview.setReviewerName(SessionUtil.getUserDetail().getUsername());
        trainingRecordReview.setSignature(optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : null);

        trainingRecordReviewRepository.save(trainingRecordReview);

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

        return "redirect:/employees/review";
    }

    @PostMapping("/employees/jd")
    public String assignEmployeeJD(UserJobDescription userJobDescription) {
        userJobDescription.setEmployeeName(userRepository.findByUserId(userJobDescription.getUsername()).getEngName());
        userJobDescription.setStatus(JobDescriptionStatus.ASSIGNED);
        userJobDescriptionRepository.save(userJobDescription);

        //TODO 알림 전송(JD 배정 알림)
        return "redirect:/employees/jd/approved";
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
            userJobDescription.setManagerTitle(StringUtils.isEmpty(SessionUtil.getUserDetail().getUser().getComPosition()) ? "N/A" : SessionUtil.getUserDetail().getUser().getComPosition());//TODO Job Title 확인 필요

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
