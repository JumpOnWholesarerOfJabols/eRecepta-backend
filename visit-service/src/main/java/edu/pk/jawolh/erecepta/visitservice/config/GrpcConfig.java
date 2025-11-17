package edu.pk.jawolh.erecepta.visitservice.config;

import edu.pk.jawolh.erecepta.common.user.proto.DoctorExistsGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {
    @Bean
    public DoctorExistsGrpc.DoctorExistsBlockingStub grpcDoctorExistsStub(GrpcChannelFactory channels) {
        return DoctorExistsGrpc.newBlockingStub(channels.createChannel("doctor"));
    }

}
