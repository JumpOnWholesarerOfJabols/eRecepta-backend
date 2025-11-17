package edu.pk.jawolh.erecepta.identityservice.service;

import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsRequest;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcServerService extends DoctorExistsGrpc.DoctorExistsImplBase {
    private final UserRepository userRepository;

    @Override
    public void checkDoctorExists(DoctorExistsRequest request, StreamObserver<DoctorExistsReply> responseObserver) {
        String doctorID = request.getDoctorId();
        boolean result = userRepository.existsByIdAndRole(UUID.fromString(doctorID), UserRole.DOCTOR);

        DoctorExistsReply reply = DoctorExistsReply.newBuilder().setDoctorExists(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
