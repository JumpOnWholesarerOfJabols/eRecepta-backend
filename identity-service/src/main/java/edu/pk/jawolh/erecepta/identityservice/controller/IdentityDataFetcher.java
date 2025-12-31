package edu.pk.jawolh.erecepta.identityservice.controller;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@DgsComponent
@RequiredArgsConstructor
public class IdentityDataFetcher {

    private final AuthService authService;

    @DgsMutation
    public Message register(@InputArgument RegisterInput input){
        String message = authService.registerUser(
                input.getEmail(),
                input.getPesel(),
                input.getFirstName(),
                input.getLastName(),
                input.getPhoneNumber(),
                input.getGender(),
                input.getDateOfBirth(),
                input.getPassword(),
                getClientIp()
        );

        return Message.newBuilder()
                .message(message).build();
    }

    @DgsMutation
    public Message verifyAccount(@InputArgument VerifyInput input){

        String message = authService.verifyAccount(
                input.getLogin(),
                input.getCode(),
                getClientIp());

        return Message.newBuilder()
                .message(message).build();
    }

    @DgsMutation
    public AuthToken login(@InputArgument LoginInput input){

        return authService.login(
                input.getLogin(),
                input.getPassword(),
                getClientIp());
    }

    @DgsMutation
    public AuthToken refreshToken(@InputArgument String refreshToken) {
        return authService.refreshToken(
                refreshToken,
                getClientIp());
    }

    @DgsMutation
    public Message logout(@InputArgument String refreshToken) {
        String message = authService.logout(
                refreshToken,
                getClientIp());

        return Message.newBuilder()
                .message(message)
                .build();
    }

    @DgsMutation
    public Message logoutFromOtherDevices(@InputArgument String refreshToken) {
        String message = authService.logoutFromOtherDevices(
                refreshToken,
                getClientIp());

        return Message.newBuilder()
                .message(message)
                .build();
    }

    @DgsMutation
    public Message requestPasswordReset(@InputArgument ResetPasswordRequestInput input){
        String message = authService.resetPasswordRequest(
                input.getLogin(),
                getClientIp());

        return Message.newBuilder().message(message).build();
    }

    @DgsMutation
    public Message resetPassword(@InputArgument ResetPasswordInput input){
        String message = authService.resetPassword(
                input.getLogin(),
                input.getPassword(),
                input.getCode(),
                getClientIp()
        );

        return Message.newBuilder().message(message).build();
    }

    @DgsMutation
    public Message sendVerificationCodeRequest(@InputArgument SendVerificationCodeRequestInput input){
        String message = authService.sendVerificationCode(
                input.getLogin(),
                getClientIp());

        return Message.newBuilder().message(message).build();
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String xForwardedForHeader = request.getHeader("X-Forwarded-For");
            if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
                return xForwardedForHeader.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return "UNKNOWN";
    }
}
