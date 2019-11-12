package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.BinderCv;
import com.dtnsm.lms.repository.BinderCvRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinderCvService {

    BinderCvRepository repository;

    public BinderCvService(BinderCvRepository repository) {
        this.repository = repository;
    }

    public BinderCv getUserActiveCv(String userId) {
        return repository.findByAccount_UserIdAndIsActive(userId, "1");
    }

    public List<BinderCv> getList() {

        return repository.findAll(new Sort(Sort.Direction.DESC, "ver"));
    }

    public List<BinderCv> getUserCvListOrderByVerDesc(String userId) {

        return repository.findAllByAccount_UserIdOrderByVerDesc(userId);
    }

    public Page<BinderCv> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return repository.findAll(pageable);
    }

    public BinderCv getById(Long id) {

        return repository.findById(id).get();
    }

    public BinderCv save(BinderCv obj){

        return repository.save(obj);
    }

    public void delete(BinderCv obj) {
        // 게시판 데이터및 파일 데이터 delete
        repository.delete(obj);
    }

    public long getCount() {
        return repository.count();
    }

}
