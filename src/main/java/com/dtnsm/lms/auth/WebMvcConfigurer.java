package com.dtnsm.lms.auth;//package com.dtnsm.elearning.auth;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//class WebMvcConfig implements WebMvcConfigurer {
//
//    @Autowired
//    @Qualifier(value = "httpInterceptor")
//    private HandlerInterceptor interceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(interceptor)
//                .excludePathPatterns("/file_uploader_html5/**")
//                        .addPathPatterns("/**");
//    }
//}
