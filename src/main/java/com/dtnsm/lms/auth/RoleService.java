package com.dtnsm.lms.auth;

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

    public Role save(Role role){

        return roleRepository.save(role);
    }

    public void delete(Role role) {
        roleRepository.delete(role);

    }
}
