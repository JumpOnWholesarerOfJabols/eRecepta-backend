package edu.pk.jawolh.erecepta.identityservice.grpc;

import com.example.demo.codegen.types.Gender;
import com.google.protobuf.Empty;
import edu.pk.jawolh.erecepta.common.user.enums.UserRole;
import edu.pk.jawolh.erecepta.common.user.proto.*;
import edu.pk.jawolh.erecepta.identityservice.exception.MultiFieldValidationException;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import edu.pk.jawolh.erecepta.identityservice.service.AuthService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcServerService extends UserServiceGrpc.UserServiceImplBase {
    private final AuthService authService;
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
                    .setId(acc.getId().toString())
                    .setEmail(acc.getEmail())
                    .setFirstName(acc.getFirstName())
                    .setLastName(acc.getLastName())
                    .setPhoneNumber(acc.getPhoneNumber())
                    .setPesel(acc.getPesel())
                    .setRole(acc.getRole().ordinal())
                    .setGender(acc.getUserGender().ordinal())
                    .setDateOfBirth(acc.getDateOfBirth().toString())
                    .setVerified(acc.isVerified())
                    .build();

            responseObserver.onNext(reply);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserReply> responseObserver) {
        CreateUserReply.Builder reply = CreateUserReply.newBuilder();
        try {
            String msg = authService.registerUser(
                    request.getEmail(),
                    request.getPesel(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhoneNumber(),
                    Gender.values()[request.getGender()],
                    request.getDateOfBirth(),
                    request.getPassword(),
                    UserRole.values()[request.getRole()],
                    "gRPC"
            );

            reply.setSuccess(true);
            reply.setMessage(msg);
        } catch (MultiFieldValidationException e) {
            reply.setSuccess(false);
            reply.setMessage(e.getMessage());
            reply.putAllErrors(e.getErrors());
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAllUsers(Empty request, StreamObserver<GetUserDataReply> responseObserver) {
        userRepository.findAll().forEach(user -> {
            GetUserDataReply.Builder reply = GetUserDataReply.newBuilder();
            reply.setId(user.getId().toString());
            reply.setEmail(user.getEmail());
            reply.setFirstName(user.getFirstName());
            reply.setLastName(user.getLastName());
            reply.setPhoneNumber(user.getPhoneNumber());
            reply.setPesel(user.getPesel());
            reply.setRole(user.getRole().ordinal());
            reply.setGender(user.getUserGender().ordinal());
            reply.setDateOfBirth(user.getDateOfBirth().toString());
            reply.setVerified(user.isVerified());

            responseObserver.onNext(reply.build());
        });
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserReply> responseObserver) {
        log.info("Received deleteUser request, userId={}", request.getId());
        DeleteUserReply.Builder reply = DeleteUserReply.newBuilder();
        UUID id;
        try {
            id = UUID.fromString(request.getId());

            if (!userRepository.existsById(id)) {
                log.warn("User not found, userId={}", id);
                reply.setSuccess(false);
                reply.setMessage("User does not exist");
            } else {
                userRepository.deleteById(id);

                log.info("User successfully deleted, userId={}", id);
                reply.setSuccess(true);
                reply.setMessage("User deleted");
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", request.getId(), e);
            reply.setSuccess(false);
            reply.setMessage("Invalid user ID format");
        } catch (Exception e) {
            log.error("Unexpected error while deleting user: {}", request.getId(), e);
            reply.setSuccess(false);
            reply.setMessage("Internal server error: " + e.getMessage());
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    private boolean existsByIdAndRole(UUID id, UserRole role) {
        Optional<UserAccount> user = userRepository.findById(id);

        return user.filter(userAccount ->
                        userAccount.isVerified() && userAccount.getRole().equals(role))
                .isPresent();
    }
}
