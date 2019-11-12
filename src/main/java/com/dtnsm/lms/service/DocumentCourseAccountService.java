package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.DocumentCourseAccount;
import com.dtnsm.lms.repository.DocumentCourseAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentCourseAccountService {

    DocumentCourseAccountRepository courseAccountRepository;

    public DocumentCourseAccountService(DocumentCourseAccountRepository courseAccountRepository) {
        this.courseAccountRepository = courseAccountRepository;
    }

    public List<DocumentCourseAccount> getList() {
        return courseAccountRepository.findAll();
    }

    public DocumentCourseAccount getById(Long id) {

        return courseAccountRepository.findById(id).get();
    }

    public DocumentCourseAccount save(DocumentCourseAccount documentCourseAccount){
        return courseAccountRepository.save(documentCourseAccount);
    }

    public void delete(DocumentCourseAccount documentCourseAccount) {
        courseAccountRepository.delete(documentCourseAccount);
    }



}
