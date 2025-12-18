package edu.pk.jawolh.erecepta.med_docs_service.config;

import edu.pk.jawolh.erecepta.common.medication.proto.MedicationServiceGrpc;
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

//    @Bean
//    public MedicationServiceGrpc.MedicationServiceBlockingStub grpcMedicationServiceStub(GrpcChannelFactory channels) {
//        return MedicationServiceGrpc.newBlockingStub(channels.createChannel("medication"));
//    }

}
