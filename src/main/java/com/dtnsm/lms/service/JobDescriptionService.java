package com.dtnsm.lms.service;

import com.dtnsm.common.entity.JobDescription;
import com.dtnsm.common.entity.QJobDescription;
import com.dtnsm.common.repository.JobDescriptionRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobDescriptionService {

    private final JobDescriptionRepository repository;

    public List<JobDescription> getList() {
        return repository.findAll();
    }

    public Page<JobDescription> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "condate"));

        return repository.findAll(pageable);
    }

    public Optional<JobDescription> findByShortName(String shortName) {
        QJobDescription qJobDescription = QJobDescription.jobDescription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qJobDescription.shortName.eq(shortName));
        return repository.findOne(builder);
    }

    public JobDescription getById(Integer id) {

        return repository.findById(id).get();
    }

    public JobDescription save(JobDescription obj){
        return repository.save(obj);
    }

//    public void delete(JobDescription obj) {
//        repository.delete(obj);
//    }



}
