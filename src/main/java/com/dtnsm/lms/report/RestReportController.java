package com.dtnsm.lms.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@Slf4j
public class RestReportController {

//    private final TrainingLogReportRepository jdbcBookRepository;
//
//    @PostConstruct
//    public void init() {
//        log.info("RestBookController init");
//    }
//
//    @PreDestroy
//    public void destroy() {
////        log.info("RestBookController destroy");
//    }
//
//    @GetMapping("/list")
////    @JsonView(ModelJsonView.Simple.class)
//    public List<Book> borderList(){
//
//        List<Book> books = jdbcBookRepository.findAll();
//
//        return books;
//    }

}
