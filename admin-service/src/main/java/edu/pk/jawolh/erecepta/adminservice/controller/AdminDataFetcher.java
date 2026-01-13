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
import edu.pk.jawolh.erecepta.adminservice.service.AdminService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class AdminDataFetcher {

    private final AdminService adminService;

    @DgsQuery
    public List<User> getAllUsers() {

        return adminService.getAllUsers();
    }

    @DgsMutation
    public CreateUserResult createUser(@InputArgument CreateUserInput input) {
        return adminService.createUser(input);
    }

    @DgsMutation
    public DeleteUserResult deleteUser(@InputArgument String userId) {

        return adminService.deleteUser(userId);
    }
}
