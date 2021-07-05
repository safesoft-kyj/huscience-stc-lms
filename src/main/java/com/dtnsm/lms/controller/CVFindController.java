package com.dtnsm.lms.controller;

import com.dtnsm.lms.data.CVCodeList;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CurriculumVitae;
import com.dtnsm.lms.domain.QCurriculumVitae;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;
import com.dtnsm.lms.mybatis.dto.CVFindParam;
import com.dtnsm.lms.mybatis.dto.CVFindResult;
import com.dtnsm.lms.mybatis.mapper.CVFinderMapper;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.CurriculumVitaeService;
import com.dtnsm.lms.util.DocumentConverter;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.dtnsm.lms.xdocreport.CurriculumVitaeReportService;
import com.dtnsm.lms.xdocreport.dto.CV;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"param", "taList", "indicationMap"})
@Slf4j
public class CVFindController {
    private PageInfo pageInfo = new PageInfo();
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final CurriculumVitaeReportService curriculumVitaeReportService;
    private final CVFinderMapper cvFinderMapper;
    private final CVCodeList cvCodeList;
    private final CurriculumVitaeService curriculumVitaeService;
    private final DocumentConverter documentConverter;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        pageInfo.setParentId("m-emp");
        pageInfo.setParentTitle("Employees");

        pageInfo.setPageId("m-finder");
        pageInfo.setPageTitle("CV Finder");
    }

    @GetMapping({"/finder", "/{employees}/finder"})
    public String finder(@PathVariable(value = "employees", required = false) String employees, Model model) {
        if(StringUtils.isEmpty(employees)) {
            pageInfo.setParentId("m-emp");
            pageInfo.setParentTitle("Employees");
        } else {
            pageInfo.setParentId("m-teamDept");
            pageInfo.setParentTitle("Team/Department");
        }

        CVFindParam cvFindParam = new CVFindParam();
        cvFindParam.setEmpStatus(1);

        model.addAttribute(pageInfo);
        model.addAttribute("cvFindParam", cvFindParam);
        model.addAttribute("indicationList", cvCodeList.getIndicationList());
//        model.addAttribute("taList", cvCodeList.getTaList());
//        model.addAttribute("indicationMap", cvCodeList.getIndicationMap());
        return "content/finder/condition";
    }



    @PostMapping({"/finder", "/{employees}/finder"})
    public String finder(@PathVariable(value = "employees", required = false) String employees,
                         @ModelAttribute("cvFindParam") CVFindParam param, Model model) {
        if(StringUtils.isEmpty(employees)) {
            pageInfo.setParentId("m-emp");
            pageInfo.setParentTitle("Employees");
        } else {
            pageInfo.setParentId("m-teamDept");
            pageInfo.setParentTitle("Team/Department");
        }
        model.addAttribute(pageInfo);
//        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;

        // 검색어 없을 경우
        if(StringUtils.isEmpty(param.getTa())) {
            param.setTa(null);
        }
        if(StringUtils.isEmpty(param.getIndication())) {
            param.setIndication(null);
        }
        if(StringUtils.isEmpty(param.getName())) {
            param.setName(null);
        }

        // Team/Department MENU
        if(!StringUtils.isEmpty(employees)) {
//         List<String> usernameList = cvFinderMapper.findByParentUserId(SessionUtil.getUserId());
//         param.setUsernameList(usernameList);
           Optional<List<Account>> optionalAccounts = userRepository.findByParentUserId(SessionUtil.getUserId());
           if (optionalAccounts.isPresent()) {
               List<Account> accounts = optionalAccounts.get();
               List<String> usernameList = accounts.stream().map(u -> u.getUserId()).collect(Collectors.toList());
               param.setUsernameList(usernameList);
           }
        }

        List<CVFindResult> resultList = cvFinderMapper.findCV(param);

        List<Integer> cvIds = resultList.stream().filter(cv -> (cv.getDays() / 365) >= param.getCareer()).map(CVFindResult::getId)
                .collect(Collectors.toList());

        if(!ObjectUtils.isEmpty(cvIds)) {
            builder.and(qCurriculumVitae.id.in(cvIds));
            if(!StringUtils.isEmpty(param.getName()))
                builder.and(qCurriculumVitae.account.engName.contains(param.getName()));
            Iterable<CurriculumVitae> cvList = curriculumVitaeRepository.findAll(builder);

            model.addAttribute("cvList", cvList);
        }

        model.addAttribute("indicationList", cvCodeList.getIndicationList());

        return "content/finder/condition";
    }

    @PostMapping({"/finder/blindcv", "/employees/finder/blindcv"})
    public void getBlindCV(@RequestParam("username") String username,
                           @RequestParam(value = "blind", defaultValue = "true") boolean blind,
                           HttpServletResponse httpServletResponse) throws Exception {

        Account account = userRepository.findByUserId(username);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\""+(blind ? "Blind" : account.getEngName())+"_CV(" + username + ").pdf\"");

        Optional<CurriculumVitae> optionalCurriculumVitae = curriculumVitaeRepository.findTop1ByAccountAndStatusOrderByIdDesc(account, CurriculumVitaeStatus.CURRENT);
        if(optionalCurriculumVitae.isPresent()) {
            CurriculumVitae curriculumVitae = optionalCurriculumVitae.get();
            CV cv = curriculumVitaeService.toCV(curriculumVitae, blind);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean result = curriculumVitaeReportService.assembleDocument(cv, os);
//            log.info("Generate Result : {}", result);
            if(result) {
//                log.info("BlindCV -> Word to PDF...");
//                File outputPdf = new File(prop.getCvUploadDir() + outputFileName);
                documentConverter.word2pdf(new ByteArrayInputStream(os.toByteArray()), httpServletResponse.getOutputStream());
//                log.info("Blind CV -> Word to PDF...Done.");
            }
        }
    }


    @PostMapping({"/finder/export", "/{employees}/finder/export"})
    public void exportCV(@PathVariable(value = "employees", required = false) String employees
            , @ModelAttribute("cvFindParam") CVFindParam param
            , Model model, HttpServletResponse response){

        if(StringUtils.isEmpty(param.getTa())) {
            param.setTa(null);
        }
        if(StringUtils.isEmpty(param.getIndication())) {
            param.setIndication(null);
        }
        if(StringUtils.isEmpty(param.getName())) {
            param.setName(null);
        }
    }


}
