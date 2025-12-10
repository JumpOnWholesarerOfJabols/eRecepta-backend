package edu.pk.jawolh.erecepta.adminservice.client;

import com.example.demo.codegen.types.CreateUserInput;
import com.example.demo.codegen.types.CreateUserResult;
import com.example.demo.codegen.types.DeleteUserResult;
import com.example.demo.codegen.types.User;
import com.google.protobuf.Empty;
import edu.pk.jawolh.erecepta.adminservice.mapper.UserMapper;
import edu.pk.jawolh.erecepta.common.user.proto.CreateUserReply;
import edu.pk.jawolh.erecepta.common.user.proto.DeleteUserReply;
import edu.pk.jawolh.erecepta.common.user.proto.DeleteUserRequest;
import edu.pk.jawolh.erecepta.common.user.proto.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GrpcUserClient {
    private final UserServiceGrpc.UserServiceBlockingStub grpcUserServiceStub;
    private final UserMapper userMapper;

    public CreateUserResult createUser(CreateUserInput input) {
        CreateUserReply r = grpcUserServiceStub.createUser(userMapper.createUserFromGraphQL(input));
        return userMapper.createUserResultFromGRPC(r);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        grpcUserServiceStub.getAllUsers(Empty.getDefaultInstance())
                .forEachRemaining(u -> users.add(userMapper.userFromGRPC(u)));
        return users;
    }

    public DeleteUserResult deleteUser(String id) {
        DeleteUserReply r = grpcUserServiceStub.deleteUser(DeleteUserRequest.newBuilder().setId(id).build());
        return userMapper.deleteUserResultFromGRPC(r);
    }

}
