package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.QTrainingRecord;
import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.TrainingRecord;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.entity.constant.TrainingRecordStatus;
import com.dtnsm.common.repository.TrainingRecordRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.domain.constant.BinderAlarmType;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.domain.constant.TrainingRecordReviewStatus;
import com.dtnsm.lms.properties.FileUploadProperties;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewJdRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DigitalBinderController {
    private PageInfo pageInfo = new PageInfo();
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final UserJobDescriptionRepository userJobDescriptionRepository;
    private final TrainingRecordRepository trainingRecordRepository;
    private final TrainingRecordReviewRepository trainingRecordReviewRepository;
    private final TrainingRecordReviewJdRepository trainingRecordReviewJdRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final FileUploadProperties properties;


    private final JPAQueryFactory queryFactory;

    @Value("${binder.cv}")
    private String digitalCv;

    @Value("${binder.jd}")
    private String digitalJd;

    @Value("${binder.log-sop}")
    private String digitalLogSop;

    @Value("${binder.log-tm}")
    private String digitalLogTm;

    @Value("${binder.cert}")
    private String digitalCert;

    @Value("${server.link}")
    private String serverLink;

    @PostConstruct
    public void init() {
        pageInfo.setParentId("m-binder");
        pageInfo.setParentTitle("Digital Binder");
    }

    @GetMapping("/binder")
    public String digitalBinder(Model model) {
        pageInfo.setPageId("h-history");
        pageInfo.setPageTitle("Training Recode Review History");

        model.addAttribute(pageInfo);
        model.addAttribute("serverLink",serverLink);

        String userId = SessionUtil.getUserId();
        Optional<CurriculumVitae> optionalCurriculumVitae = getCurrentCurriculumVitae(userId);
        if(optionalCurriculumVitae.isPresent()) {
            CurriculumVitae cv = optionalCurriculumVitae.get();
            if(!cv.isReviewed()) {
                model.addAttribute("cv", cv);
            }
        }
        Iterable<UserJobDescription> userJobDescriptions = getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED));
        model.addAttribute("jdList", userJobDescriptions);

        Optional<TrainingRecord> optionalTrainingRecord = getTrainingRecord(userId);
        model.addAttribute("trainingRecord", optionalTrainingRecord.isPresent() ? optionalTrainingRecord.get() : null);

        QTrainingRecordReview qTrainingRecordReview = QTrainingRecordReview.trainingRecordReview;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qTrainingRecord.sopStatus.eq(TrainingRecordStatus.PUBLISHED_SOP));
//        builder.and(qTrainingRecord.tmStatus.eq(TrainingRecordStatus.PUBLISHED_SOP));
        builder.and(qTrainingRecordReview.account.userId.eq(userId));
//        QTrainingRecordReview qTrainingRecordReview = QTrainingRecordReview.trainingRecordReview;
        Iterable<TrainingRecordReview> trainingRecordReviewList = trainingRecordReviewRepository.findAll(builder, qTrainingRecordReview.id.desc());
        model.addAttribute("trainingRecordReviewList", trainingRecordReviewList);
//      model.addAttribute("oldJdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED)));

        Account account = SessionUtil.getUserDetail().getUser();
        model.addAttribute("engName", account.getEngName());
        model.addAttribute("inDate", StringUtils.isEmpty(account.getIndate()) ? "" : DateUtil.getDateToString(DateUtil.getStringToDate(account.getIndate(), "yyyy-MM-dd"), "dd-MMM-yyyy").toUpperCase());
        model.addAttribute("deptTeam", (StringUtils.isEmpty(account.getOrgDepart()) ? "" : account.getOrgDepart()) +
                (!StringUtils.isEmpty(account.getOrgDepart()) && !StringUtils.isEmpty(account.getOrgTeam()) ? "/" : "") + (StringUtils.isEmpty(account.getOrgTeam()) ? "" : account.getOrgTeam()));

        model.addAttribute("empNo", account.getComNum());

        if(!ObjectUtils.isEmpty(userJobDescriptions)) {
            model.addAttribute("jobTitle", StreamSupport.stream(userJobDescriptions.spliterator(), false)
                    .map(u -> u.getJobDescriptionVersion().getJobDescription().getTitle()).collect(Collectors.joining(",")));
        } else {
            model.addAttribute("jobTitle", "N/A");
        }
        return "content/binder/digitalBinder";
    }

    @Transactional
    @PostMapping("/binder/review")
    public String requestReview(RedirectAttributes attributes) {
        String userId = SessionUtil.getUserId();
        TrainingRecordReview trainingRecordReview = new TrainingRecordReview();
        Account account = SessionUtil.getUserDetail().getUser();
        trainingRecordReview.setAccount(account);

        boolean isCV = false;
        boolean isTR = false;

        Optional<CurriculumVitae> optionalCV = getCurrentCurriculumVitae(userId);
        if(optionalCV.isPresent()) {
            CurriculumVitae cv = optionalCV.get();
            if(!cv.isReviewed()) {
                isCV = true;
                trainingRecordReview.setCurriculumVitae(cv);

            }
        }
        Optional<TrainingRecord> optionalTrainingRecord = getTrainingRecord(userId);
        if(optionalTrainingRecord.isPresent()) {
            isTR = true;
            TrainingRecord trainingRecord = optionalTrainingRecord.get();
            trainingRecordReview.setTrainingRecord(trainingRecord);
        }

        if(isCV == false && isTR == false) {
            attributes.addFlashAttribute("returnMessage", "?????? ??? ????????? ???????????? ????????????. ?????? ??? ?????? ?????? ????????????.");
        } else {
            trainingRecordReview.setRequestDate(new Date());
            trainingRecordReview.setStatus(TrainingRecordReviewStatus.REQUEST);
            TrainingRecordReview savedTrainingRecordReview = trainingRecordReviewRepository.save(trainingRecordReview);
            Iterable<UserJobDescription> jobDescriptions = getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED));
            if(!ObjectUtils.isEmpty(jobDescriptions)) {
                List<UserJobDescription> jobDescriptionList = StreamSupport.stream(jobDescriptions.spliterator(), false)
                        .filter(jd -> !jd.isReviewed())
                        .collect(Collectors.toList());

                for(UserJobDescription userJobDescription : jobDescriptionList) {
                    QTrainingRecordReviewJd qTrainingRecordReviewJd = QTrainingRecordReviewJd.trainingRecordReviewJd;
                    BooleanBuilder jdBuilder = new BooleanBuilder();
                    jdBuilder.and(qTrainingRecordReviewJd.userJobDescription.id.eq(userJobDescription.getId()));
                    //TODO
                    jdBuilder.and(qTrainingRecordReviewJd.trainingRecordReview.status.eq(TrainingRecordReviewStatus.REVIEWED));
                    Optional<TrainingRecordReviewJd> optionalTrainingRecordReviewJd = trainingRecordReviewJdRepository.findOne(jdBuilder);
                    if(optionalTrainingRecordReviewJd.isPresent() == false) {
                        TrainingRecordReviewJd trainingRecordReviewJd = new TrainingRecordReviewJd();
                        trainingRecordReviewJd.setTrainingRecordReview(savedTrainingRecordReview);
                        trainingRecordReviewJd.setUserJobDescription(userJobDescription);

                        trainingRecordReviewJdRepository.save(trainingRecordReviewJd);
                    }
                }
            }
//cLab??????
            if(!StringUtils.isEmpty(account.getParentUserId())) {
                attributes.addFlashAttribute("returnMessage", "??????????????? ????????? ?????????????????????.");
                String toEmail = userRepository.findByUserId(account.getParentUserId()).getEmail();
                Context context = new Context();
                context.setVariable("empName", account.getName());
                context.setVariable("inDate", StringUtils.isEmpty(account.getIndate()) ? "N/A" : DateUtil.getDateToString(DateUtil.getStringToDate(account.getIndate()), "yyyy-MM-dd"));
                mailService.send(toEmail, String.format(BinderAlarmType.BINDER_REVIEW.getTitle(), account.getName()), BinderAlarmType.BINDER_REVIEW, context);
            } else {
                log.error("????????? ????????? ?????? ?????? ????????????.");
                attributes.addFlashAttribute("returnMessage", "????????? ????????? ?????? ?????? ????????????.");
            }
        }


        return "redirect:/binder";
    }

    @GetMapping("/binder/{id}/download")
    public void download(@PathVariable("id") Integer id, HttpServletResponse response) throws Exception {
        TrainingRecordReview trainingRecordReview = trainingRecordReviewRepository.findById(id).get();
        String path = properties.getBinderDir() + trainingRecordReview.getBinderPdf();
//        log.info("@download path : {}", path);
        Account account = SessionUtil.getUserDetail().getUser();

        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=DigitalBinder("+account.getUserId()+").pdf");
        OutputStream os = response.getOutputStream();
        os.write(new FileInputStream(new File(path)).readAllBytes());
        os.flush();
        os.close();
    }

    @GetMapping("/binder/cv")
    public String cv(Model model) {
        pageInfo.setPageId("d-cv");
        pageInfo.setPageTitle(digitalCv);

        model.addAttribute(pageInfo);

        String userId = SessionUtil.getUserId();
        model.addAttribute("cvList", getCurriculumVitaeList(userId, Arrays.asList(CurriculumVitaeStatus.CURRENT, CurriculumVitaeStatus.SUPERSEDED)));
        return "content/binder/cv";
    }

    @GetMapping("/binder/jd")
    public String jd(Model model) {
        pageInfo.setPageId("d-jd");
        pageInfo.setPageTitle(digitalJd);

        model.addAttribute(pageInfo);

        String userId = SessionUtil.getUserId();
        model.addAttribute("jdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED)));
        model.addAttribute("oldJdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED)));
        return "content/binder/jd";
    }

    /**
     * ????????? ???????????? ?????? CV ??????
     * @param userId
     * @return
     */
    private Optional<CurriculumVitae> getCurrentCurriculumVitae(String userId) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.status.eq(CurriculumVitaeStatus.CURRENT));
        builder.and(qCurriculumVitae.account.userId.eq(userId));
//        builder.and(qCurriculumVitae.reviewed.eq(false));
        return curriculumVitaeRepository.findOne(builder);
    }

    private Iterable<CurriculumVitae> getCurriculumVitaeList(String userId, List<CurriculumVitaeStatus> statusList) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        if(statusList.size() == 1) {
            builder.and(qCurriculumVitae.status.eq(statusList.get(0)));
        } else {
            builder.and(qCurriculumVitae.status.in(statusList));
        }
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        return curriculumVitaeRepository.findAll(builder, qCurriculumVitae.id.desc());
    }

    /**
     * @param userId
     * @param statusList
     * @return
     */
    private Iterable<UserJobDescription> getJobDescriptionList(String userId, List<JobDescriptionStatus> statusList) {
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        if(statusList.size() == 1) {
            builder.and(qUserJobDescription.status.eq(statusList.get(0)));
        } else {
            builder.and(qUserJobDescription.status.in(statusList));
        }
        builder.and(qUserJobDescription.username.eq(userId));
//        builder.and(qUserJobDescription.reviewed.eq(false));

        return userJobDescriptionRepository.findAll(builder, qUserJobDescription.id.desc());
    }

    private Optional<TrainingRecord> getTrainingRecord(String userId) {
        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTrainingRecord.username.eq(userId));
        builder.and(qTrainingRecord.status.eq(TrainingRecordStatus.PUBLISHED));
        return trainingRecordRepository.findOne(builder);
    }

    @GetMapping("/binder/{type}/trainingLog")
    public String getSopTrainingLog(@PathVariable("type") String type, Model model) {
        pageInfo.setPageId("t-log");
        if(type.toUpperCase().equals("SOP"))     pageInfo.setPageTitle(digitalLogSop);
        else                                     pageInfo.setPageTitle(digitalLogTm);

        model.addAttribute(pageInfo);

        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qTrainingRecord.status.notIn(TrainingRecordStatus.REJECTED));//????????? ????????? ???????????? ??????????????? ??????
        if("sop".equals(type)) {
            builder.and(qTrainingRecord.sopFileName.isNotEmpty());
        } else if("tm".equals(type)) {
            builder.and(qTrainingRecord.tmFileName.isNotEmpty());
        }

        model.addAttribute("type", type);

        builder.and(qTrainingRecord.username.eq(SessionUtil.getUserId()));
        Iterable<TrainingRecord> trainingRecords = trainingRecordRepository.findAll(builder, qTrainingRecord.id.desc());
        if(ObjectUtils.isEmpty(trainingRecords)) {
            model.addAttribute("trainingRecord", null);
        } else {
            model.addAttribute("trainingRecord", trainingRecords);
        }

        return "content/binder/trainingLog";
    }

    @GetMapping("/binder/certification")
    public String getCertification(Model model) {
        pageInfo.setPageId("t-cert");
        pageInfo.setPageTitle(digitalCert);
        model.addAttribute(pageInfo);

        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qTrainingRecord.tmStatus.in(TrainingRecordStatus.REVIEW, TrainingRecordStatus.REVIEWED, TrainingRecordStatus.PUBLISHED));
        builder.and(qTrainingRecord.tmCertHtmlContent.isNotEmpty());
        builder.and(qTrainingRecord.username.eq(SessionUtil.getUserId()));
        Iterable<TrainingRecord> trainingRecords = trainingRecordRepository.findAll(builder, qTrainingRecord.id.desc());
        if(!ObjectUtils.isEmpty(trainingRecords)) {
            Iterator i = trainingRecords.iterator();
            if(i.hasNext()) {
                model.addAttribute("cert", i.next());
            } else {
                model.addAttribute("cert", null);
            }
        }

        return "content/binder/certification";
    }

    @GetMapping("/admin/binder")
    public String binderFinder(@RequestParam(value = "empStatus", required = false, defaultValue = "1") int empStatus
            , @RequestParam(value = "empId", required = false, defaultValue = "") String empId
            , Model model){
        pageInfo.setPageId("h-history");
        pageInfo.setPageTitle("Training Record Review History");

        // empNameList
        List<Account> empRetireList = userRepository.findAllByEnabledOrderByNameAsc(false);
        List<Account> empPresentList = userRepository.findAllByEnabledOrderByNameAsc(true);

        model.addAttribute(pageInfo);
        model.addAttribute("empRetireList", empRetireList);
        model.addAttribute("empPresentList", empPresentList);
        return "admin/binder/finder/list";
    }

    @PostMapping("/admin/binder")
    public String binderFinderResult(@RequestParam(value = "empStatus", required = false, defaultValue = "1") int empStatus
            , @RequestParam(value = "empId", required = false, defaultValue = "") String empId
            , Model model){
        pageInfo.setPageId("h-history");
        pageInfo.setPageTitle("Training Recode Review History");

        List<Account> empRetireList = userRepository.findAllByEnabledOrderByNameAsc(false);
        List<Account> empPresentList = userRepository.findAllByEnabledOrderByNameAsc(true);

        model.addAttribute(pageInfo);
        model.addAttribute("empRetireList", empRetireList);
        model.addAttribute("empPresentList", empPresentList);

        if( empId != null && !empId.isEmpty()){

            // ?????? emp ??????
            Account account = userRepository.findByUserId(empId);
            model.addAttribute("username", account.getEngName());
            model.addAttribute("inDate", StringUtils.isEmpty(account.getIndate()) ? "" : DateUtil.getDateToString(DateUtil.getStringToDate(account.getIndate(), "yyyy-MM-dd"), "dd-MMM-yyyy").toUpperCase());
            model.addAttribute("deptTeam", (StringUtils.isEmpty(account.getOrgDepart()) ? "" : account.getOrgDepart()) +
                    (!StringUtils.isEmpty(account.getOrgDepart()) && !StringUtils.isEmpty(account.getOrgTeam()) ? "/" : "") + (StringUtils.isEmpty(account.getOrgTeam()) ? "" : account.getOrgTeam()));
            model.addAttribute("empNo", account.getComNum());

            // ?????? emp ??? JD
            Iterable<UserJobDescription> userJobDescriptions = getJobDescriptionList(empId, Arrays.asList(JobDescriptionStatus.APPROVED));
            if(!ObjectUtils.isEmpty(userJobDescriptions)) {
                model.addAttribute("jobTitle", StreamSupport.stream(userJobDescriptions.spliterator(), false)
                        .map(u -> u.getJobDescriptionVersion().getJobDescription().getTitle()).collect(Collectors.joining(",")));
            } else {
                model.addAttribute("jobTitle", "N/A");
            }

            // ?????? emp??? trainingRecord review
            QTrainingRecordReview qTrainingRecordReview = QTrainingRecordReview.trainingRecordReview;
            QAccount qAccount = QAccount.account;
            List<TrainingRecordReview> trainingRecordReviewList = queryFactory.select(qTrainingRecordReview)
                    .from(qTrainingRecordReview)
                    .where(qAccount.userId.eq(empId))
                    .fetch();

            model.addAttribute("trainingRecordReviewList", trainingRecordReviewList);
        }

        return "admin/binder/finder/list";
    }
}
