package edu.pk.jawolh.erecepta.visitservice.service;

import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsRequest;
import edu.pk.jawolh.erecepta.common.user.proto.UserServiceGrpc;
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
}
