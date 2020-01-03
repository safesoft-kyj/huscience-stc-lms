package com.dtnsm.lms.controller;

import com.dtnsm.lms.data.CVCodeList;
import com.dtnsm.lms.domain.CurriculumVitae;
import com.dtnsm.lms.domain.QCurriculumVitae;
import com.dtnsm.lms.mybatis.dto.CVFindParam;
import com.dtnsm.lms.mybatis.dto.CVFindResult;
import com.dtnsm.lms.mybatis.mapper.CVFinderMapper;
import com.dtnsm.lms.repository.CurriculumVitaeRepository;
import com.dtnsm.lms.util.PageInfo;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"param", "taList", "indicationMap"})
public class CVFindController {
    private PageInfo pageInfo = new PageInfo();
    private final CurriculumVitaeRepository curriculumVitaeRepository;
    private final CVFinderMapper cvFinderMapper;
    private final CVCodeList cvCodeList;

    @PostConstruct
    public void init() {
        pageInfo.setParentId("m-emp");
        pageInfo.setParentTitle("Employees");

        pageInfo.setPageId("m-finder");
        pageInfo.setPageTitle("CV Finder");
    }

    @GetMapping("/finder")
    public String finder(Model model) {
        model.addAttribute(pageInfo);
        model.addAttribute("cvFindParam", new CVFindParam());
        model.addAttribute("taList", cvCodeList.getTaList());
        model.addAttribute("indicationMap", cvCodeList.getIndicationMap());
        return "content/finder/condition";
    }



    @PostMapping("/finder")
    public String finder(@ModelAttribute("cvFindParam") CVFindParam param, Model model) {
        model.addAttribute(pageInfo);
//        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;
        BooleanBuilder builder = new BooleanBuilder();
        QCurriculumVitae qCurriculumVitae = QCurriculumVitae.curriculumVitae;

//        CVFindParam param = new CVFindParam();
        if(StringUtils.isEmpty(param.getTa())) {
            param.setTa(null);
        }
        if(StringUtils.isEmpty(param.getIndication())) {
            param.setIndication(null);
        }

        List<CVFindResult> resultList = cvFinderMapper.findCV(param);

        List<Integer> cvIds = resultList.stream().filter(cv -> (cv.getDays() / 365) >= param.getCareer()).map(CVFindResult::getId)
                .collect(Collectors.toList());
        if(!ObjectUtils.isEmpty(cvIds)) {
            builder.and(qCurriculumVitae.id.in(cvIds));
            Iterable<CurriculumVitae> cvList = curriculumVitaeRepository.findAll(builder);

            model.addAttribute("cvList", cvList);
        }
        return "content/finder/condition";
    }
}
