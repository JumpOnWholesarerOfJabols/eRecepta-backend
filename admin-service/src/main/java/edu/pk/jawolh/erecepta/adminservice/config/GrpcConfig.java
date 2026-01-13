package edu.pk.jawolh.erecepta.adminservice.config;

import edu.pk.jawolh.erecepta.common.user.proto.UserServiceGrpc;
import edu.pk.jawolh.erecepta.common.visit.proto.VisitServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {
    @Bean
    public UserServiceGrpc.UserServiceBlockingStub grpcUserServiceStub(GrpcChannelFactory channels) {
        return UserServiceGrpc.newBlockingStub(channels.createChannel("user"));
    }

    @Bean
    public VisitServiceGrpc.VisitServiceBlockingStub grpcVisitServiceStub(GrpcChannelFactory channels) {
        return VisitServiceGrpc.newBlockingStub(channels.createChannel("visit"));
    }
}
