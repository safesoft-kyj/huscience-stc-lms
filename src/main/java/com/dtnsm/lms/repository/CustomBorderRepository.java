package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Border;

import java.util.List;

public interface CustomBorderRepository {

    List<Border> findAllBorderByViewUser(String userId);

}