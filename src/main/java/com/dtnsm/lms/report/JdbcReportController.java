package com.dtnsm.lms.report;

import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-04-28 오후 3:22
 * @desc Github : https://github.com/pub147
 **/
@RequestMapping("/admin/report")
@Controller
public class JdbcReportController {

    @Autowired
    private TrainingLogReportRepository trainingLogReportRepository;

    private final PageInfo pageInfo = new PageInfo();

    public JdbcReportController() {
        pageInfo.setParentId("m-border");
        pageInfo.setParentTitle("Report");
    }

    @GetMapping("/training/head")
    public String trainingHead(Model model) {

        List<Map<String, Object>> logList = trainingLogReportRepository.findLogByUser("%");

        pageInfo.setPageTitle("Training Summary");

        model.addAttribute(pageInfo);
        model.addAttribute("logList", logList);

        return "admin/report/training-head";
    }

    @GetMapping({"/training/detail", "/training/detail/{userId}"})
    public String trainingDetail(@RequestParam(value = "userId", defaultValue = "%", required = false) String userId, Model model) {

        List<Map<String, Object>> logList = trainingLogReportRepository.findLogDetailByUser(userId);
        pageInfo.setPageTitle("Training Log");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", logList);

        return "admin/report/training-detail";
    }

    @GetMapping("/certificate/head")
    public String certificateHead(Model model) {

        List<Map<String, Object>> logList = trainingLogReportRepository.findCertByUser("%");

        pageInfo.setPageTitle("수료증 Summary");

        model.addAttribute(pageInfo);
        model.addAttribute("logList", logList);

        return "admin/report/certificate-head";
    }

    @GetMapping({"/certificate/detail", "/certificate/detail/{userId}"})
    public String certificateDetail(@RequestParam(value = "userId", defaultValue = "%", required = false) String userId, Model model) {

        List<Map<String, Object>> logList = trainingLogReportRepository.findCertDetailByUser(userId);
        pageInfo.setPageTitle("수료증");

        model.addAttribute(pageInfo);
        model.addAttribute("borders", logList);

        return "admin/report/certificate-detail";
    }

}
