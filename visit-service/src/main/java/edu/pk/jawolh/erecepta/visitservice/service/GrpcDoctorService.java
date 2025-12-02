package edu.pk.jawolh.erecepta.visitservice.service;

import edu.pk.jawolh.erecepta.common.user.proto.UserExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcDoctorService {
    private final UserExistsGrpc.UserExistsBlockingStub grpcDoctorExistsStub;

    public boolean checkDoctorExists(String uuid) {
        UserExistsRequest request = UserExistsRequest.newBuilder().setUserId(uuid).build();
        UserExistsReply response = grpcDoctorExistsStub.checkDoctorExists(request);
        return response.getUserExists();
    }
}
