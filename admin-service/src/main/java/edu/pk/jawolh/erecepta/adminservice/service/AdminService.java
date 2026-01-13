package edu.pk.jawolh.erecepta.adminservice.service;

import com.example.demo.codegen.types.CreateUserInput;
import com.example.demo.codegen.types.CreateUserResult;
import com.example.demo.codegen.types.DeleteUserResult;
import com.example.demo.codegen.types.User;
import edu.pk.jawolh.erecepta.adminservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.adminservice.client.GrpcVisitClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final GrpcUserClient grpcUserClient;
    private final GrpcVisitClient grpcVisitClient;

    public List<User> getAllUsers() {
        return grpcUserClient.getAllUsers();
    }

    public CreateUserResult createUser(CreateUserInput input) {
        return grpcUserClient.createUser(input);
    }

    public DeleteUserResult deleteUser(String userId) {
        DeleteUserResult deleteUserResult = grpcUserClient.deleteUser(userId);

        if (deleteUserResult.getSuccess())
            grpcVisitClient.cancelVisitsByUserId(userId);

        return deleteUserResult;
    }
}
