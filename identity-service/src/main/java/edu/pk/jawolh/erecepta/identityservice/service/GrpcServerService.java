package edu.pk.jawolh.erecepta.identityservice.service;

import edu.pk.jawolh.erecepta.common.user.proto.*;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcServerService extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepository userRepository;

    @Override
    public void checkDoctorExists(DoctorExistsRequest request, StreamObserver<DoctorExistsReply> responseObserver) {
        String doctorID = request.getDoctorId();
        boolean result = userRepository.existsByIdAndRole(UUID.fromString(doctorID), UserRole.DOCTOR);

        DoctorExistsReply reply = DoctorExistsReply.newBuilder().setDoctorExists(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserData(GetUserDataRequest request, StreamObserver<GetUserDataReply> responseObserver) {
        String userId = request.getUserId();
        Optional<UserAccount> opt = userRepository.findById(UUID.fromString(userId));

        if (opt.isEmpty())
            responseObserver.onNext(GetUserDataReply.newBuilder().build());
        else {
            UserAccount acc = opt.get();
            GetUserDataReply reply = GetUserDataReply.newBuilder()
                    .setEmail(acc.getEmail())
                    .setFirstName(acc.getFirstName())
                    .setLastName(acc.getLastName())
                    .build();

            responseObserver.onNext(reply);
        }

        responseObserver.onCompleted();
    }
}
