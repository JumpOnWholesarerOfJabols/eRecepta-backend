package edu.pk.jawolh.erecepta.identityservice.mapper;

import com.example.demo.codegen.types.LoginAttempt;

public class LoginAttemptMapper {

    public static LoginAttempt toDTO(edu.pk.jawolh.erecepta.identityservice.model.LoginAttempt domainAttempt) {
        if (domainAttempt == null) {
            return null;
        }

        return LoginAttempt.newBuilder()
                .id(domainAttempt.getId().toString())
                .userId(domainAttempt.getUserId().toString())
                .ipAddress(domainAttempt.getIpAddress())
                .success(domainAttempt.isSuccess())
                .attemptDate(domainAttempt.getAttemptDate().toString())
                .build();
    }
}