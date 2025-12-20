package edu.pk.jawolh.erecepta.prescriptionservice.config;

import edu.pk.jawolh.erecepta.common.medication.proto.MedicationServiceGrpc;
import edu.pk.jawolh.erecepta.common.record.proto.PatientRecordServiceGrpc;
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
    public MedicationServiceGrpc.MedicationServiceBlockingStub grpcMedicationServiceStub(GrpcChannelFactory channels) {
        return MedicationServiceGrpc.newBlockingStub(channels.createChannel("medication"));
    }

    @Bean
    public VisitServiceGrpc.VisitServiceBlockingStub grpcVisitServiceStub(GrpcChannelFactory channels) {
        return VisitServiceGrpc.newBlockingStub(channels.createChannel("visit"));
    }

    @Bean
    public PatientRecordServiceGrpc.PatientRecordServiceBlockingStub grpcRecordServiceStub(GrpcChannelFactory channels) {
        return PatientRecordServiceGrpc.newBlockingStub(channels.createChannel("record"));
    }

}
