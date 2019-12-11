package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.QTrainingRecord;
import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.TrainingRecord;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.entity.constant.TrainingRecordStatus;
import com.dtnsm.common.repository.TrainingRecordRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.domain.CurriculumVitae;
import com.dtnsm.lms.domain.QCurriculumVitae;
import com.dtnsm.lms.domain.TrainingRecordReview;
import com.dtnsm.lms.domain.TrainingRecordReviewJd;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Iterator;
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
        model.addAttribute("cvList", getCurriculumVitaeList(userId, Arrays.asList(CurriculumVitaeStatus.CURRENT)));
        model.addAttribute("jdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED)));

        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(qTrainingRecord.sopStatus.eq(TrainingRecordStatus.PUBLISHED_SOP));
//        builder.and(qTrainingRecord.tmStatus.eq(TrainingRecordStatus.PUBLISHED_SOP));
        builder.and(qTrainingRecord.username.eq(userId));
        Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findOne(builder);
        if(optionalTrainingRecord.isPresent()) {
            TrainingRecord trainingRecord = optionalTrainingRecord.get();
//            if(!StringUtils.isEmpty(trainingRecord.getSopFileName()) && !StringUtils.isEmpty(trainingRecord.getTmFileName())) {
                model.addAttribute("trainingRecord", trainingRecord);
//            }
        }
//        model.addAttribute("oldJdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED)));
        return "content/binder/digitalBinder";
    }

    @Transactional
    @PostMapping("/binder/review")
    public String requestReview(RedirectAttributes attributes) {
        String userId = SessionUtil.getUserId();
        TrainingRecordReview trainingRecordReview = new TrainingRecordReview();
        trainingRecordReview.setUsername(userId);


        Iterable<CurriculumVitae> curriculumVitaes = getCurriculumVitaeList(userId, Arrays.asList(CurriculumVitaeStatus.CURRENT));
        Iterator<CurriculumVitae> iter = curriculumVitaes.iterator();
        if(iter.hasNext()) {
            trainingRecordReview.setCurriculumVitae(iter.next());
        }

        BooleanBuilder builder = new BooleanBuilder();
        QTrainingRecord qTrainingRecord = QTrainingRecord.trainingRecord;
        builder.and(qTrainingRecord.username.eq(userId));
        builder.and(qTrainingRecord.sopStatus.eq(TrainingRecordStatus.PUBLISHED_SOP));
        builder.and(qTrainingRecord.tmStatus.eq(TrainingRecordStatus.PUBLISHED_TM));
        Optional<TrainingRecord> optionalTrainingRecord = trainingRecordRepository.findOne(builder);
        if(optionalTrainingRecord.isPresent()) {
            trainingRecordReview.setTrainingRecord(optionalTrainingRecord.get());
        }

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

    private Iterable<UserJobDescription> getJobDescriptionList(String userId, List<JobDescriptionStatus> statusList) {
        QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        if(statusList.size() == 1) {
            builder.and(qUserJobDescription.status.eq(statusList.get(0)));
        } else {
            builder.and(qUserJobDescription.status.in(statusList));
        }
        builder.and(qUserJobDescription.username.eq(userId));

        return userJobDescriptionRepository.findAll(builder, qUserJobDescription.id.desc());
    }
}
