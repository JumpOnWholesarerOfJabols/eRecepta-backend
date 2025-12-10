package edu.pk.jawolh.erecepta.adminservice.mapper;

import com.example.demo.codegen.types.*;
import edu.pk.jawolh.erecepta.common.user.proto.CreateUserReply;
import edu.pk.jawolh.erecepta.common.user.proto.CreateUserRequest;
import edu.pk.jawolh.erecepta.common.user.proto.DeleteUserReply;
import edu.pk.jawolh.erecepta.common.user.proto.GetUserDataReply;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User userFromGRPC(GetUserDataReply input) {
        return User.newBuilder()
                .id(input.getId())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .email(input.getEmail())
                .pesel(input.getPesel())
                .phoneNumber(input.getPhoneNumber())
                .role(Role.values()[input.getRole()])
                .gender(Gender.values()[input.getGender()])
                .dateOfBirth(input.getDateOfBirth())
                .build();
    }

    public CreateUserRequest createUserFromGraphQL(CreateUserInput input) {
        return CreateUserRequest.newBuilder()
                .setEmail(input.getEmail())
                .setFirstName(input.getFirstName())
                .setLastName(input.getLastName())
                .setDateOfBirth(input.getDateOfBirth())
                .setGender(input.getGender().ordinal())
                .setRole(input.getRole().ordinal())
                .setPhoneNumber(input.getPhoneNumber())
                .setPassword(input.getPassword())
                .setPesel(input.getPesel())
                .build();
    }

    public CreateUserResult createUserResultFromGRPC(CreateUserReply input) {
        return CreateUserResult.newBuilder().success(input.getSuccess()).message(input.getMessage()).build();
    }

    public DeleteUserResult deleteUserResultFromGRPC(DeleteUserReply input) {
        return DeleteUserResult.newBuilder().success(input.getSuccess()).message(input.getMessage()).build();
    }
}
