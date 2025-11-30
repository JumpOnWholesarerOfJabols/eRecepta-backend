package edu.pk.jawolh.erecepta.visitservice.service;

import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcDoctorService {
    private final DoctorExistsGrpc.DoctorExistsBlockingStub grpcDoctorExistsStub;

    public boolean checkDoctorExists(String uuid) {
        DoctorExistsRequest request = DoctorExistsRequest.newBuilder().setDoctorId(uuid).build();
        DoctorExistsReply response = grpcDoctorExistsStub.checkDoctorExists(request);
        return response.getDoctorExists();
    }
}
