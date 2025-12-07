package edu.pk.jawolh.erecepta.patientrecordservice.config;

import edu.pk.jawolh.erecepta.common.user.proto.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {
    @Bean
    public UserServiceGrpc.UserServiceBlockingStub grpcUserServiceStub(GrpcChannelFactory channels) {
        return UserServiceGrpc.newBlockingStub(channels.createChannel("user"));
    }

}
