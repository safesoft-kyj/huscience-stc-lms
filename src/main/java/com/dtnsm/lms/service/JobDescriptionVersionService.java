package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.JobDescriptionVersionFile;
import com.dtnsm.lms.domain.JobDescriptionVersion;
import com.dtnsm.lms.repository.JobDescriptionVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobDescriptionVersionService {

    JobDescriptionVersionRepository repository;

    JobDescriptionFileService descriptionFileService;


    public JobDescriptionVersionService(JobDescriptionVersionRepository repository) {
        this.repository = repository;
    }

    public List<JobDescriptionVersion> getList() {
        return repository.findAll();
    }

    public List<JobDescriptionVersion> getListByJdIdOrderByVerDesc(long jdId) {

        return repository.findAllByJd_IdOrderByVerDesc(jdId);
    }

    public JobDescriptionVersion getByJdIdAndActiveJd(long jdId) {
        return repository.findByJd_IdAndIsActive(jdId, "1");
    }

    public List<JobDescriptionVersion> getListByAcitveJd(String isActive) {
        return repository.findAllByIsActive(isActive);
    }

    public Page<JobDescriptionVersion> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "condate"));

        return repository.findAll(pageable);
    }

    public JobDescriptionVersion getById(Long id) {

        return repository.findById(id).get();
    }

    public JobDescriptionVersion save(JobDescriptionVersion obj){
        return repository.save(obj);
    }

    public void delete(JobDescriptionVersion obj) {

        // 첨부파일 삭제
        for(JobDescriptionVersionFile jdFile : obj.getJdFiles()) {
            descriptionFileService.deleteFile(jdFile);
        }

        repository.delete(obj);
    }
}
