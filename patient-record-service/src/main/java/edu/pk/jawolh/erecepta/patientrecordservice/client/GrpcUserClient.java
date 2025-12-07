package edu.pk.jawolh.erecepta.patientrecordservice.client;

import edu.pk.jawolh.erecepta.common.user.proto.UserExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcUserClient {
    private final UserExistsGrpc.UserExistsBlockingStub stub;

    public boolean isPatient(String UUID) {
        UserExistsRequest request = UserExistsRequest.newBuilder().setUserId(UUID).build();
        UserExistsReply response = stub.checkPatientExists(request);
        return response.getUserExists();
    }
}
