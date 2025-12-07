package edu.pk.jawolh.erecepta.visitservice.client;

import edu.pk.jawolh.erecepta.common.user.proto.UserExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcUserClient {
    private final UserExistsGrpc.UserExistsBlockingStub grpcDoctorExistsStub;

    public boolean checkDoctorExists(String UUID) {
        UserExistsRequest request = UserExistsRequest.newBuilder().setUserId(UUID).build();
        UserExistsReply response = grpcDoctorExistsStub.checkDoctorExists(request);
        return response.getUserExists();
    }
}
