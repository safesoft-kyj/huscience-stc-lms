package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    public Privilege findByName(String name);
}
