package edu.pk.jawolh.erecepta.med_docs_service.client;

import edu.pk.jawolh.erecepta.common.user.proto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcUserClient {
    private final UserServiceGrpc.UserServiceBlockingStub stub;

    public boolean isPatient(String UUID) {
        UserExistsRequest request = UserExistsRequest.newBuilder().setUserId(UUID).build();
        UserExistsReply response = stub.checkPatientExists(request);
        return response.getUserExists();
    }

    public boolean isPharmacist(String UUID) {
        UserExistsRequest request = UserExistsRequest.newBuilder().setUserId(UUID).build();
        UserExistsReply response = stub.checkPharmacistExists(request);
        return response.getUserExists();
    }

    public boolean isDoctor(String UUID) {
        UserExistsRequest request = UserExistsRequest.newBuilder().setUserId(UUID).build();
        UserExistsReply response = stub.checkDoctorExists(request);
        return response.getUserExists();
    }

    public GetUserDataReply getUserData(String UUID) {
        GetUserDataRequest request = GetUserDataRequest.newBuilder().setUserId(UUID).build();
        return stub.getUserData(request);
    }
}
