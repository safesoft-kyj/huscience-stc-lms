package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.BinderCv;
import com.dtnsm.lms.domain.BinderJd;
import com.dtnsm.lms.repository.BinderJdRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinderJdService {

    BinderJdRepository repository;

    public BinderJdService(BinderJdRepository repository) {
        this.repository = repository;
    }

    public BinderJd getUserActiveJd(String userId) {
        return repository.findByAccount_UserIdAndIsActive(userId, "1");
    }

    public BinderJd getByUserActiveVersionJd(String userId, long versionId) {
        return repository.findByAccount_UserIdAndJdVersion_IdAndIsActive(userId, versionId, "1");
    }

    public List<BinderJd> getAllByUserActiveJd(String userId) {
        return repository.findAllByAccount_UserIdAndIsActive(userId, "1");
    }

    public List<BinderJd> getList() {

        return repository.findAll(new Sort(Sort.Direction.DESC, "ver"));
    }

    public List<BinderJd> getListByJdVersionId(long versionId) {
        return repository.findAllByJdVersion_Id(versionId);
    }

    public List<BinderJd> getUserJdListOrderByVerDesc(String userId) {

        return repository.findAllByAccount_UserId(userId);
    }

    public List<BinderJd> getListByIsActiveJd() {

        return repository.findAllByIsActive("1");
    }

    public Page<BinderJd> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "createdDate"));

        return repository.findAll(pageable);
    }

    public BinderJd getById(Long id) {

        return repository.findById(id).get();
    }

    public BinderJd save(BinderJd obj){

        return repository.save(obj);
    }

    public void delete(BinderJd obj) {
        // 게시판 데이터및 파일 데이터 delete
        repository.delete(obj);
    }

    public long getCount() {
        return repository.count();
    }

}
