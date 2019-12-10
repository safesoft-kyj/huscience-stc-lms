package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.domain.CurriculumVitae;
import com.dtnsm.lms.domain.QCurriculumVitae;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DigitalBinderController {
    private PageInfo pageInfo = new PageInfo();
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final UserJobDescriptionRepository userJobDescriptionRepository;

    @PostConstruct
    public void init() {
        pageInfo.setParentId("m-binder");
        pageInfo.setParentTitle("Digital Binder");
    }

    @GetMapping("/binder")
    public String digitalBinder(Model model) {
        model.addAttribute(pageInfo);

        String userId = SessionUtil.getUserId();
        model.addAttribute("cvList", getCurriculumVitaeList(userId));
        model.addAttribute("jdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED)));
        model.addAttribute("oldJdList", getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.SUPERSEDED, JobDescriptionStatus.REVOKED)));
        return "content/binder/digitalBinder";
    }

    @GetMapping("/binder/cv")
    public String cv(Model model) {
        model.addAttribute(pageInfo);

        String userId = SessionUtil.getUserId();
        model.addAttribute("cvList", getCurriculumVitaeList(userId));
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
    private Iterable<CurriculumVitae> getCurriculumVitaeList(String userId) {
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCurriculumVitae.status.in(CurriculumVitaeStatus.CURRENT, CurriculumVitaeStatus.SUPERSEDED));
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
