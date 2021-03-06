package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.ElMajor;
import com.dtnsm.lms.domain.ElMinor;
import com.dtnsm.lms.domain.ElMinorId;
import com.dtnsm.lms.repository.ElMajorRepository;
import com.dtnsm.lms.repository.ElMinorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class CodeService {

    @Autowired
    ElMajorRepository majorRepository;

    @Autowired
    ElMinorRepository minorRepository;

    public List<ElMajor> getMajorList() {

        return majorRepository.findAll();
    }

    public ElMajor getMajorById(String id) {

        return majorRepository.getOne(id);
    }

    public ElMajor majorSave(ElMajor major){

        return majorRepository.save(major);
    }

    public void majorDelete(ElMajor major) {

        majorRepository.delete(major);
    }

    public List<ElMinor> getMinorList() {

        return minorRepository.findAll();
    }

    public ElMinor minorSave(ElMinor elbMinor){

        return minorRepository.save(elbMinor);
    }

    public void minorDelete(ElMinor elMinor) {
        minorRepository.delete(elMinor);
    }

    public void minorDelete(String minorCd) {

        ElMinor elMinor = minorRepository.getOne (minorCd);

        this.minorDelete(elMinor);
    }

    public ElMinor getMinorById(String id) {

        return minorRepository.getOne(id);
    }

//    public ElMinor getMinorByMajorCdAndMinorCd(String majorCd, String minorCd) {
//
//        ElMinorId elMinorId = this.getElMinorId(majorCd, minorCd);
//
//        return elMinorRepository.getOne(elMinorId);
//    }

    public ElMinorId getMinorId(String majorCd, String minorCd) {
        ElMinorId elMinorId = new ElMinorId();
        elMinorId.setMajorCd(majorCd);
        elMinorId.setMinorCd(minorCd);

        return elMinorId;

    }

    public List<ElMinor> getMinorList(String majorCd) {
        return minorRepository.findByElMajor_MajorCd(majorCd);
    }


    // ?????? ??????
    public HashMap<String, String > getManualCodeType() {
        HashMap<String, String > map = new HashMap<>();
        map.put("S", "?????????");
        map.put("M", "?????????");
        map.put("C", "?????????");

        return map;
    }


}
