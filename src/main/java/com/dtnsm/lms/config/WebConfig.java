package com.dtnsm.lms.config;

import com.dtnsm.lms.view.ExcelXlsView;
import com.dtnsm.lms.view.ExcelXlsxStreamingView;
import com.dtnsm.lms.view.ExcelXlsxView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ExcelXlsView excelXlsView;

    @Autowired
    private ExcelXlsxView excelXlsxView;

    @Autowired
    private ExcelXlsxStreamingView excelXlsxStreamingView;

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        //registry.enableContentNegotiation(excelXlsView, excelXlsxView, excelXlsxStreamingView);
    }
}