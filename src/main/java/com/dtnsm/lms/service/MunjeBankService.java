package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.BorderMaster;
import com.dtnsm.lms.domain.ElMinor;
import com.dtnsm.lms.domain.MunjeBank;
import com.dtnsm.lms.domain.constant.MunjeType;
import com.dtnsm.lms.repository.BorderMasterRepository;
import com.dtnsm.lms.repository.MunjeBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunjeBankService {

    MunjeBankRepository munjeBankRepository;

    @Autowired
    BorderService borderService;

    public MunjeBankService(MunjeBankRepository munjeBankRepository) {
        this.munjeBankRepository = munjeBankRepository;
    }

    public List<MunjeBank> getList() {
        return munjeBankRepository.findAll();
    }

    public List<MunjeBank> getListByMunjeType(MunjeType munjeType) {
        return munjeBankRepository.findAllByMunjeType(munjeType);
    }

    public Page<MunjeBank> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createdDate"));

        return munjeBankRepository.findAll(pageable);
    }

    public MunjeBank getById(long id) {

        return munjeBankRepository.findById(id).get();
    }

    public MunjeBank save(MunjeBank munjeBank){

        return munjeBankRepository.save(munjeBank);
    }

    public void delete(MunjeBank munjeBank) {

        munjeBankRepository.delete(munjeBank);
    }
}
