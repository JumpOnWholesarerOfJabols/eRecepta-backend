package edu.pk.jawolh.erecepta.identityservice.mapper;

import com.example.demo.codegen.types.AuditLog;

public class AuditLogMapper {

    public static AuditLog toDTO(edu.pk.jawolh.erecepta.identityservice.model.AuditLog domainLog) {
        if (domainLog == null) {
            return null;
        }

        return AuditLog.newBuilder()
                .id(domainLog.getId().toString())
                .userId(domainLog.getUserId().toString())
                .ipAddress(domainLog.getIpAddress())
                .actionName(domainLog.getActionName())
                .logDate(domainLog.getLogDate().toString())
                .build();
    }
}