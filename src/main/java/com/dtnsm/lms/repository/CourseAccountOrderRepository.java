package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.CourseAccount;
import com.dtnsm.lms.domain.CourseAccountOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CourseAccountOrderRepository extends JpaRepository<CourseAccountOrder, Long> {

    CourseAccountOrder findById(long id);

    CourseAccountOrder findByCourseAccount_IdAndFSeq(long fNo, int seq);

//    // 사용자가 결재루틴에 포함된 모든 문서
//    List<CourseAccountOrder> findAllByFUser_UserId(String userId);
//
//    // 사용자가 결재루틴에 포함된 모든 문서
//    Page<CourseAccountOrder> findAllByFUser_UserId(String userId, Pageable pageable);

    // 사용자가 결재루틴에 포함된 모든 문서
//    @Query("SELECT g " +
//            "FROM CourseAccount g inner join CourseAccountOrder h on g.id = h.FCourseAccount.id  " +
//            "where h.FUser.userId = :userId and g.fStatus = '1'")
//    List<CourseAccount> searchAllByApproval(String userId);
//
//    // 사용자가 결재루틴에 포함된 모든 문서
//    @Query("SELECT g " +
//            "FROM CourseAccount g inner join CourseAccountOrder h on g.id = h.FCourseAccount  " +
//            "where h.FUser.userId = :userId and g.fStatus = '2'")
//    Page<CourseAccountOrder> searchAllByReject(String userId, Pageable pageable);
//
//    // 사용자가 결재루틴에 포함된 모든 문서
//    @Query("SELECT g " +
//            "FROM CourseAccount g inner join CourseAccountOrder h on g.id = h.FCourseAccount  " +
//            "where h.FUser.userId = :userId and g.fStatus = '2'")
//    List<CourseAccountOrder> searchAllByReject(String userId);

    // 사용자가 결재한 모든 문서
    Page<CourseAccountOrder> findAllByFUser_UserIdAndFSeqGreaterThan(String userId, int seq, Pageable pageable);

    // 사용자가 결재한 모든 문서
    List<CourseAccountOrder> findAllByFUser_UserIdAndFSeqGreaterThan(String userId, int seq);

    // 사용자별 진행문서(fStatus = '0')
    Page<CourseAccountOrder> findAllByFUser_UserIdAndFStatus(String userId, String fStatus, Pageable pageable);

    // 사용자별 미결문서(fStatus = '0')
    List<CourseAccountOrder> findAllByFUser_UserIdAndFStatus(String userId, String fStatus);

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    Page<CourseAccountOrder> findAllByFUser_UserIdAndFNextAndFStatus(String userId, String fNext, String fStatus, Pageable pageable);

    // 사용자별 미결문서(fNext = '1', fStatus = '0')
    List<CourseAccountOrder> findAllByFUser_UserIdAndFNextAndFStatus(String userId, String fNext, String fStatus);


    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    Page<CourseAccountOrder> findAllByFUser_UserIdAndFNextAndFStatusAndFSeq(String userId, String fNext, String fStatus, int fSeq, Pageable pageable);

    // 결재 차수의 미결문서(fNext = '1', fStatus = '0', fSeq = 1 or 2)
    List<CourseAccountOrder> findAllByFUser_UserIdAndFNextAndFStatusAndFSeq(String userId, String fNext, String fStatus, int fSeq);




}