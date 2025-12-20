package edu.pk.jawolh.erecepta.prescriptionservice.client;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import edu.pk.jawolh.erecepta.common.visit.proto.GetVisitReply;
import edu.pk.jawolh.erecepta.common.visit.proto.GetVisitRequest;
import edu.pk.jawolh.erecepta.common.visit.proto.VisitServiceGrpc;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.VisitDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcVisitClient {
    private final VisitServiceGrpc.VisitServiceBlockingStub stub;

    public VisitDTO getVisit(UUID visitId) {
        GetVisitRequest request = GetVisitRequest.newBuilder().setVisitId(visitId.toString()).build();
        GetVisitReply response = stub.getVisit(request);

        return new VisitDTO(UUID.fromString(response.getId()), UUID.fromString(response.getPatientId()), UUID.fromString(response.getDoctorId()), LocalDateTime.parse(response.getVisitTime()), Specialization.values()[response.getSpecialization()], VisitStatus.values()[response.getVisitStatus()]);
    }
}
