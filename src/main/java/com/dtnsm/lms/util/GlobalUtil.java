package com.dtnsm.lms.util;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GlobalUtil {

    public static Signature getSignature(SignatureRepository signatureRepository, String userId) {

        // 사인정보 가져오기
        Optional<Signature> optionalSignature = signatureRepository.findById(userId);
        Signature signature = null;

       if(optionalSignature.isPresent()) {
            signature = optionalSignature.get();
        }
        return signature;
    }

}
