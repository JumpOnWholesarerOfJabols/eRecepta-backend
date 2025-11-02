package edu.pk.jawolh.erecepta.identityservice.controller;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.identityservice.service.AuthService;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class IdentityDataFetcher {

    private final AuthService authService;

    @DgsMutation
    public Message register(@InputArgument RegisterInput input){
        String message = authService.registerUser(
                input.getEmail(),
                input.getPassword(),
                input.getPesel(),
                input.getRole(),
                input.getGender());

        return Message.newBuilder()
                .message(message).build();
    }

    @DgsMutation
    public Message verifyAccount(@InputArgument VerifyInput input){
        String message = authService.verifyAccount(
                input.getLogin(),
                input.getCode());

        return Message.newBuilder()
                .message(message).build();
    }

    @DgsMutation
    public AuthToken login(@InputArgument LoginInput input){
        String token = authService.login(
                input.getLogin(),
                input.getPassword());

        return AuthToken.newBuilder()
                .token(token).build();
    }

    @DgsMutation
    public Message requestPasswordReset(@InputArgument ResetPasswordRequestInput input){
        String message = authService.resetPasswordRequest(
                input.getLogin());

        return Message.newBuilder().message(message).build();
    }

    @DgsMutation
    public Message resetPassword(@InputArgument ResetPasswordInput input){
        String message = authService.resetPassword(
                input.getLogin());

        return Message.newBuilder().message(message).build();
    }
}
