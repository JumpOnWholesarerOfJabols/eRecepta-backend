package edu.pk.jawolh.erecepta.visitservice.service;

import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcDoctorService {
    private final DoctorExistsGrpc.DoctorExistsBlockingStub grpcDoctorExistsStub;

    boolean checkDoctorExists(String UUID) {
        log.debug("Checking if doctor exists: {}", UUID);
        try {
            DoctorExistsRequest request = DoctorExistsRequest.newBuilder().setDoctorId(UUID).build();
            DoctorExistsReply response = grpcDoctorExistsStub.checkDoctorExists(request);
            boolean exists = response.getDoctorExists();
            log.debug("Doctor exists check result for {}: {}", UUID, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking if doctor exists: {}", UUID, e);
            throw e;
        }
    }
}
