package com.dtnsm.lms.service;

import com.dtnsm.lms.domain.Privilege;
import com.dtnsm.lms.repository.PrivilegeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivilegeService {

    PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public List<Privilege> getList() {
        return privilegeRepository.findAll();
    }

    public Privilege getRoleById(Long id) {

        return privilegeRepository.findById(id).get();
    }

    public Privilege save(Privilege privilege){

        return privilegeRepository.save(privilege);
    }

    public void delete(Privilege privilege) {
        privilegeRepository.delete(privilege);

    }
}
