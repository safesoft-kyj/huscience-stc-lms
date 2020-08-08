package com.dtnsm.lms.util;

import com.dtnsm.lms.domain.mapper.BorderMasterMapper;
import com.dtnsm.lms.domain.mapper.CourseMasterMapper;
import com.dtnsm.lms.repository.BorderMasterRepository;
import com.dtnsm.lms.repository.CourseMasterRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-08-08 오후 1:49
 * @desc Github : https://github.com/pub147
 **/
@Component
public class MenuUtil {
    private static BorderMasterRepository borderMasterRepository;
    private static CourseMasterRepository courseMasterRepository;

    public MenuUtil(BorderMasterRepository borderMasterRepository
        , CourseMasterRepository courseMasterRepository) {
        this.borderMasterRepository = borderMasterRepository;
        this.courseMasterRepository = courseMasterRepository;
    }

    /**
     * 메뉴에서 사용할 게시판 리스트를 리턴한다.
     * @param
     * @return List<BorderMaster>
     * @exception
     * @see
     */
    public static List<BorderMasterMapper> getBorderMaster() {
        return borderMasterRepository.findAllBy();
    }

    /**
     * 메뉴에서 사용할 교육과정을 리스트를 리턴한다.
     * @param
     * @return
     * @exception
     * @see
     */
    public static List<CourseMasterMapper> getCourseMaster() {
        return courseMasterRepository.findAllBy();
    }
}
