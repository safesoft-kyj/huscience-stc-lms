package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.ElMinor;
import com.dtnsm.lms.domain.BorderMaster;
import com.dtnsm.lms.repository.BorderMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorderMasterService {

    BorderMasterRepository borderMasterRepository;

    @Autowired
    BorderService borderService;

    @Autowired
    CodeService codeService;

    private static String majorCd = "BA01";

    public BorderMasterService(BorderMasterRepository borderMasterRepository) {
        this.borderMasterRepository = borderMasterRepository;
    }

    public List<BorderMaster> getList() {
        return borderMasterRepository.findAll();
    }

    public Page<BorderMaster> getPageList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createdDate"));

        return borderMasterRepository.findAll(pageable);
    }

    public BorderMaster getById(String id) {

        return borderMasterRepository.getOne(id);
    }

    public BorderMaster save(BorderMaster borderGroup){

        return borderMasterRepository.save(borderGroup);
    }

    public void delete(BorderMaster borderGroup) {

        borderMasterRepository.delete(borderGroup);
    }

    public List<ElMinor> getTypeList() {
        return codeService.getMinorList(majorCd);
    }

    public String getBorderNumber() {
        return String.format("%s%02d", majorCd, borderMasterRepository.count() + 1);
    }
}
