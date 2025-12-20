package edu.pk.jawolh.erecepta.prescriptionservice.client;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import edu.pk.jawolh.erecepta.common.visit.proto.*;
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

    public boolean checkVisitExists(UUID visitId, UUID doctorId) {
        CheckVisitExistsRequest request = CheckVisitExistsRequest.newBuilder().setVisitId(visitId.toString()).setDoctorId(doctorId.toString()).build();
        CheckVisitExistsReply response = stub.checkVisitExists(request);

        return response.getExists();
    }
}
