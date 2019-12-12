package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.DocumentTemplate;
import com.dtnsm.lms.repository.DocumentTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentTemplateService {

    DocumentTemplateRepository templateRepository;

    public DocumentTemplateService(DocumentTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public List<DocumentTemplate> getList() {
        return templateRepository.findAll();
    }

    public Page<DocumentTemplate> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createdDate"));

        return templateRepository.findAll(pageable);
    }

    public DocumentTemplate getById(int id) {

        return templateRepository.findById(id).get();
    }

    public DocumentTemplate save(DocumentTemplate template){

        return templateRepository.save(template);
    }

    public void delete(DocumentTemplate template) {

        templateRepository.delete(template);
    }
}
