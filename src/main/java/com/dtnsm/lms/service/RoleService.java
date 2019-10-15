package com.dtnsm.lms.service;

import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.domain.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getList() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {

        return roleRepository.findById(id).get();
    }

    public Role getRoleByName(String name){
        return roleRepository.findByName(name);
    }

    public Role save(Role role){

        return roleRepository.save(role);
    }

    public void delete(Role role) {
        roleRepository.delete(role);

    }
}
