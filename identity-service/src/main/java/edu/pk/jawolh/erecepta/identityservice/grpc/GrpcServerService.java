package edu.pk.jawolh.erecepta.identityservice.grpc;

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
    public void checkDoctorExists(UserExistsRequest request, StreamObserver<UserExistsReply> responseObserver) {
        UUID userId = UUID.fromString(request.getUserId());
        boolean result = existsByIdAndRole(userId, UserRole.DOCTOR);

        UserExistsReply reply = UserExistsReply.newBuilder().setUserExists(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void checkPatientExists(UserExistsRequest request, StreamObserver<UserExistsReply> responseObserver) {
        UUID userId = UUID.fromString(request.getUserId());
        boolean result = existsByIdAndRole(userId, UserRole.PATIENT);

        UserExistsReply reply = UserExistsReply.newBuilder().setUserExists(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void checkPharmacistExists(UserExistsRequest request, StreamObserver<UserExistsReply> responseObserver) {
        UUID userId = UUID.fromString(request.getUserId());
        boolean result = existsByIdAndRole(userId, UserRole.PHARMACIST);

        UserExistsReply reply = UserExistsReply.newBuilder().setUserExists(result).build();
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

    private boolean existsByIdAndRole(UUID id, UserRole role) {
        Optional<UserAccount> user = userRepository.findById(id);

        return user.filter(userAccount ->
                        userAccount.isVerified() && userAccount.getRole().equals(role))
                .isPresent();
    }
}
