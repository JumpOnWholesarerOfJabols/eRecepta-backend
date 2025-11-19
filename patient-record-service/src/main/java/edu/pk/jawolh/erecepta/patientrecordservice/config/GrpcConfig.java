package edu.pk.jawolh.erecepta.patientrecordservice.config;

import edu.pk.jawolh.erecepta.common.user.proto.UserExistsGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {
    @Bean
    public UserExistsGrpc.UserExistsBlockingStub grpcDoctorExistsStub(GrpcChannelFactory channels) {
        return UserExistsGrpc.newBlockingStub(channels.createChannel("patient-record-service"));
    }

}
