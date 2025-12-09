package edu.pk.jawolh.erecepta.adminservice.client;

import edu.pk.jawolh.erecepta.common.user.proto.*;
import edu.pk.jawolh.erecepta.common.visit.dtos.UserDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcUserClient {
    private final UserServiceGrpc.UserServiceBlockingStub grpcUserServiceStub;

    public boolean checkDoctorExists(String uuid) {
        UserExistsRequest request = UserExistsRequest.newBuilder().setUserId(uuid).build();
        UserExistsReply response = grpcUserServiceStub.checkDoctorExists(request);
        return response.getUserExists();
    }

    public UserDataDTO getUserData(String uuid) {
        GetUserDataRequest request = GetUserDataRequest.newBuilder().setUserId(uuid).build();
        GetUserDataReply response = grpcUserServiceStub.getUserData(request);
        return new UserDataDTO(response.getFirstName(), response.getLastName(), response.getEmail());
    }
}
