package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.BinderCv;
import com.dtnsm.lms.domain.BinderCvEducation;
import com.dtnsm.lms.repository.BinderCvEducationRepository;
import com.dtnsm.lms.repository.BinderCvRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinderCvEducationService {

    BinderCvEducationRepository repository;

    public BinderCvEducationService(BinderCvEducationRepository repository) {
        this.repository = repository;
    }

    public List<BinderCvEducation> getList() {
        return repository.findAll();
    }

    public Page<BinderCvEducation> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return repository.findAll(pageable);
    }

    public BinderCvEducation getById(Long id) {

        return repository.findById(id).get();
    }

    public BinderCvEducation save(BinderCvEducation obj){

        return repository.save(obj);
    }

    public void delete(BinderCvEducation obj) {
        // 게시판 데이터및 파일 데이터 delete
        repository.delete(obj);
    }

    public long getCount() {
        return repository.count();
    }

}
