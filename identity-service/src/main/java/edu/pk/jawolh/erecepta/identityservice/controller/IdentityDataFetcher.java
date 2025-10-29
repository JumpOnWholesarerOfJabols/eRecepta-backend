package edu.pk.jawolh.erecepta.identityservice.controller;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class IdentityDataFetcher {

    @DgsMutation
    public Message register(@InputArgument RegisterInput input){
        return Message.newBuilder().message("test").build();
    }

    @DgsMutation
    public Message verifyAccount(@InputArgument VerifyInput input){
        return Message.newBuilder().message("test").build();
    }

    @DgsMutation
    public AuthToken login(@InputArgument LoginInput input){
        return AuthToken.newBuilder().token("test").build();
    }

    @DgsMutation
    public Message requestPasswordReset(@InputArgument ResetPasswordRequestInput input){
        return Message.newBuilder().message("test").build();
    }

    @DgsMutation
    public Message resetPassword(@InputArgument ResetPasswordInput input){
        return Message.newBuilder().message("test").build();
    }
}
