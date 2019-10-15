package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByName(String name);

}
