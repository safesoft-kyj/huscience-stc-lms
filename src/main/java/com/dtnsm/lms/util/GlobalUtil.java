package com.dtnsm.lms.util;

import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class GlobalUtil {
    public static UserJobDescriptionRepository userJobDescriptionRepository;
    public GlobalUtil(UserJobDescriptionRepository userJobDescriptionRepository) {
        GlobalUtil.userJobDescriptionRepository = userJobDescriptionRepository;
    }

    public static Signature getSignature(SignatureRepository signatureRepository, String userId) {

        // 사인정보 가져오기
        Optional<Signature> optionalSignature = signatureRepository.findById(userId);
        Signature signature = null;

       if(optionalSignature.isPresent()) {
            signature = optionalSignature.get();
        }
        return signature;
    }

        public static String getJobDescriptionString(String userId) {

            if(userId.isEmpty()) return "JD";
            Iterable<UserJobDescription> userJobDescriptions = GlobalUtil.getJobDescriptionList(userId, Arrays.asList(JobDescriptionStatus.APPROVED));

            if (userJobDescriptions==null) return "JD";
            return StreamSupport.stream(userJobDescriptions.spliterator(), false)
                    .map(u -> u.getJobDescriptionVersion().getJobDescription().getShortName()).collect(Collectors.joining(","));
        }

        public static Iterable<UserJobDescription> getJobDescriptionList(String userId, List<JobDescriptionStatus> statusList) {
            QUserJobDescription qUserJobDescription = QUserJobDescription.userJobDescription;
            BooleanBuilder builder = new BooleanBuilder();
            if(statusList.size() == 1) {
                builder.and(qUserJobDescription.status.eq(statusList.get(0)));
            } else {
                builder.and(qUserJobDescription.status.in(statusList));
            }
            builder.and(qUserJobDescription.username.eq(userId));
    //        builder.and(qUserJobDescription.reviewed.eq(false));

            System.out.println(userJobDescriptionRepository);

            return userJobDescriptionRepository.findAll(builder, qUserJobDescription.id.desc());
    }

}
