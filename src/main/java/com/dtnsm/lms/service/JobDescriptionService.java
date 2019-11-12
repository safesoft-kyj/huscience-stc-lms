package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.JobDescription;
import com.dtnsm.lms.repository.JobDescriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobDescriptionService {

    JobDescriptionRepository repository;

    public JobDescriptionService(JobDescriptionRepository repository) {
        this.repository = repository;
    }

    public List<JobDescription> getList() {
        return repository.findAll();
    }

    public Page<JobDescription> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "condate"));

        return repository.findAll(pageable);
    }

    public JobDescription getById(Long id) {

        return repository.findById(id).get();
    }

    public JobDescription save(JobDescription obj){
        return repository.save(obj);
    }

    public void delete(JobDescription obj) {
        repository.delete(obj);
    }



}
