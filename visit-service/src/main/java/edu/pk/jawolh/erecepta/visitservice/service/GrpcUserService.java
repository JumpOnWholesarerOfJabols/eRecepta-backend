package edu.pk.jawolh.erecepta.visitservice.service;

import edu.pk.jawolh.erecepta.common.user.proto.*;
import edu.pk.jawolh.erecepta.common.visit.dtos.UserDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcUserService {
    private final UserServiceGrpc.UserServiceBlockingStub grpcUserServiceStub;

    public boolean checkDoctorExists(String uuid) {
        DoctorExistsRequest request = DoctorExistsRequest.newBuilder().setDoctorId(uuid).build();
        DoctorExistsReply response = grpcUserServiceStub.checkDoctorExists(request);
        return response.getDoctorExists();
    }

    public UserDataDTO getUserData(String uuid) {
        GetUserDataRequest request = GetUserDataRequest.newBuilder().setUserId(uuid).build();
        GetUserDataReply response = grpcUserServiceStub.getUserData(request);
        return new UserDataDTO(response.getFirstName(), response.getLastName(), response.getEmail());
    }
}
