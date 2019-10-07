package com.dtnsm.lms.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ElCodeService {

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


    // 코드 구분
    public HashMap<String, String > getManualCodeType() {
        HashMap<String, String > map = new HashMap<>();
        map.put("S", "시스템");
        map.put("M", "관리자");
        map.put("C", "사용자");

        return map;
    }


}
