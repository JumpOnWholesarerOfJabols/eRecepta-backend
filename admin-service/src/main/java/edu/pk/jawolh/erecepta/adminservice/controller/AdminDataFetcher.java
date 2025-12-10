package edu.pk.jawolh.erecepta.adminservice.controller;

import com.example.demo.codegen.types.CreateUserInput;
import com.example.demo.codegen.types.CreateUserResult;
import com.example.demo.codegen.types.DeleteUserResult;
import com.example.demo.codegen.types.User;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.adminservice.client.GrpcUserClient;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class AdminDataFetcher {
    private final GrpcUserClient client;

    @DgsQuery
    public List<User> getAllUsers() {
        return client.getAllUsers();
    }

    @DgsMutation
    public CreateUserResult createUser(@InputArgument CreateUserInput input) {
        return client.createUser(input);
    }

    @DgsMutation
    public DeleteUserResult deleteUser(@InputArgument String id) {
        return client.deleteUser(id);
    }
}
