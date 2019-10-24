package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.constant.ExcelConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("download")
public class DownloadExcelController {

    @GetMapping("excel-xls")
    public ModelAndView xlsView() {
        return new ModelAndView("excelXlsView", initExcelData());
    }

    @GetMapping("excel-xlsx")
    public ModelAndView xlsxView() {
        return new ModelAndView("excelXlsxView", initExcelData());
    }

    @GetMapping("excel-xlsx-streaming")
    public ModelAndView xlsxStreamingView() {
        return new ModelAndView("excelXlsxStreamingView", initExcelData());
    }

    private Map<String, Object> initExcelData() {
        Map<String, Object> map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "default_excel");
        map.put(ExcelConstant.HEAD, Arrays.asList("ID", "NAME", "COMMENT"));
        map.put(ExcelConstant.BODY,
                Arrays.asList(
                        Arrays.asList("A", "a", "가"),
                        Arrays.asList("B", "b", "나"),
                        Arrays.asList("C", "c", "다")
                ));
        return map;
    }
}