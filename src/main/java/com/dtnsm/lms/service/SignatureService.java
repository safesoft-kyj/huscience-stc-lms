package com.dtnsm.lms.service;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SignatureService {

    @Autowired
    SignatureRepository signatureRepository;

    public Signature getSignature(String userId) {

        // 사인정보 가져오기
        Optional<Signature> optionalSignature = signatureRepository.findById(userId);
        Signature signature = null;

        if(optionalSignature.isPresent()) {
            signature = optionalSignature.get();
        }

        return signature;
    }

    public String getSign(String userId) {

        // 사인정보 가져오기
        Signature signature = getSignature(userId);

        if (signature == null) return "";
        else return signature.getBase64signature();
    }
}
