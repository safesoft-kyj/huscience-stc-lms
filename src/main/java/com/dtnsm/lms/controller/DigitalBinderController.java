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
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.domain.constant.TrainingRecordReviewStatus;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewJdRepository;
import com.dtnsm.lms.repository.TrainingRecordReviewRepository;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequiredArgsConstructor
public class DigitalBinderController {
    private PageInfo pageInfo = new PageInfo();
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final UserJobDescriptionRepository userJobDescriptionRepository;
    private final TrainingRecordRepository trainingRecordRepository;
    private final TrainingRecordReviewRepository trainingRecordReviewRepository;
    private final TrainingRecordReviewJdRepository trainingRecordReviewJdRepository;

    @PostConstruct
    public void init() {
        pageInfo.setParentId("m-binder");
        pageInfo.setParentTitle("Digital Binder");
    }

    @GetMapping("/binder")
    public String digitalBinder(Model model) {
        model.addAttribute(pageInfo);

        String userId = SessionUtil.getUserId();
        Optional<CurriculumVitae> optionalCurriculumVitae = getCurrentCurriculumVitae(userId);
        model.addAttribute("cv", optionalCurriculumVitae.isPresent() ? optionalCurriculumVitae.get() : null);
        model.addAttribute("jdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED)));
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
        return "content/binder/digitalBinder";
    }

    @Transactional
    @PostMapping("/binder/review")
    public String requestReview(RedirectAttributes attributes) {
        String userId = SessionUtil.getUserId();
        TrainingRecordReview trainingRecordReview = new TrainingRecordReview();
        trainingRecordReview.setAccount(SessionUtil.getUserDetail().getUser());


        Optional<CurriculumVitae> optionalCV = getCurrentCurriculumVitae(userId);
        if(optionalCV.isPresent()) {
            trainingRecordReview.setCurriculumVitae(optionalCV.get());
        }

        BooleanBuilder builder = new BooleanBuilder();
        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        builder.and(qTrainingRecord.username.eq(userId));
//        builder.and(qTrainingRecord.sopStatus.eq(TrainingRecordStatus.PUBLISHED).or(qTrainingRecord.tmStatus.eq(TrainingRecordStatus.PUBLISHED)));
        builder.and(qTrainingRecord.status.eq(TrainingRecordStatus.PUBLISHED));
        Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findOne(builder);
        if(optionalTrainingRecord.isPresent()) {
            TrainingRecord trainingRecord = optionalTrainingRecord.get();
            trainingRecordReview.setTrainingRecord(trainingRecord);

            //training record status(review 상태로 변경)
            trainingRecord.setStatus(TrainingRecordStatus.REVIEW);
            trainingRecordRepository.save(trainingRecord);
        }

        trainingRecordReview.setRequestDate(new Date());
        trainingRecordReview.setStatus(TrainingRecordReviewStatus.REQUEST);
        TrainingRecordReview savedTrainingRecordReview = trainingRecordReviewRepository.save(trainingRecordReview);
        Iterable<UserJobDescription> jobDescriptions = getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED));
        if(!ObjectUtils.isEmpty(jobDescriptions)) {
            List<UserJobDescription> jobDescriptionList = StreamSupport.stream(jobDescriptions.spliterator(), false)
                    .collect(Collectors.toList());
            for(UserJobDescription userJobDescription : jobDescriptionList) {
                TrainingRecordReviewJd trainingRecordReviewJd = new TrainingRecordReviewJd();
                trainingRecordReviewJd.setTrainingRecordReview(savedTrainingRecordReview);
                trainingRecordReviewJd.setUserJobDescription(userJobDescription);

                trainingRecordReviewJdRepository.save(trainingRecordReviewJd);
            }
        }
        attributes.addFlashAttribute("returnMessage", "검토 요청 했습니다.");
        //TODO 알림 전송!!! 매니저에게!!
        return "redirect:/binder";
    }

    @GetMapping("/binder/cv")
    public String cv(Model model) {
        model.addAttribute(pageInfo);

        String userId = SessionUtil.getUserId();
        model.addAttribute("cvList", getCurriculumVitaeList(userId, Arrays.asList(CurriculumVitaeStatus.CURRENT, CurriculumVitaeStatus.SUPERSEDED)));
        return "content/binder/cv";
    }

    @GetMapping("/binder/jd")
    public String jd(Model model) {
        model.addAttribute(pageInfo);

        String userId = SessionUtil.getUserId();
        model.addAttribute("jdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED)));
        model.addAttribute("oldJdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED)));
        return "content/binder/jd";
    }

    /**
     * 리뷰를 진행하지 않은 CV 정보
     * @param userId
     * @return
     */
    private Optional<CurriculumVitae> getCurrentCurriculumVitae(String userId) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.status.eq(CurriculumVitaeStatus.CURRENT));
        builder.and(qCurriculumVitae.account.userId.eq(userId));
        builder.and(qCurriculumVitae.reviewed.eq(false));
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
     * 리뷰를 진행하지 않은 JD 정보
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
        builder.and(qUserJobDescription.reviewed.eq(false));

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
        model.addAttribute(pageInfo);

        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qTrainingRecord.status.in(TrainingRecordStatus.REVIEW, TrainingRecordStatus.REVIEWED));
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
}
