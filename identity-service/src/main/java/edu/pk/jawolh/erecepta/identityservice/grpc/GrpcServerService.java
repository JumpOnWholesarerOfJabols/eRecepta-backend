package edu.pk.jawolh.erecepta.identityservice.grpc;

import edu.pk.jawolh.erecepta.common.user.proto.UserExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsRequest;
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
public class GrpcServerService extends UserExistsGrpc.UserExistsImplBase {
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

    private boolean existsByIdAndRole(UUID id, UserRole role) {
        Optional<UserAccount> user = userRepository.findById(id);

        return user.filter(userAccount ->
                        userAccount.isVerified() && userAccount.getRole().equals(role))
                .isPresent();
    }
}
