package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.BinderCv;
import com.dtnsm.lms.domain.BinderCvSkills;
import com.dtnsm.lms.repository.BinderCvRepository;
import com.dtnsm.lms.repository.BinderCvSkillsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinderCvSkillsService {

    BinderCvSkillsRepository repository;

    public BinderCvSkillsService(BinderCvSkillsRepository repository) {
        this.repository = repository;
    }

    public List<BinderCvSkills> getList() {
        return repository.findAll();
    }

    public Page<BinderCvSkills> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return repository.findAll(pageable);
    }

    public BinderCvSkills getById(Long id) {

        return repository.findById(id).get();
    }

    public BinderCvSkills save(BinderCvSkills obj){

        return repository.save(obj);
    }

    public void delete(BinderCvSkills obj) {
        // 게시판 데이터및 파일 데이터 delete
        repository.delete(obj);
    }

    public long getCount() {
        return repository.count();
    }

}
