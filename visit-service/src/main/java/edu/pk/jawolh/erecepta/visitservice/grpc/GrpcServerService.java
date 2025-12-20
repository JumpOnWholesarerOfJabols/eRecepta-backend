package edu.pk.jawolh.erecepta.visitservice.grpc;

import edu.pk.jawolh.erecepta.common.visit.proto.GetVisitReply;
import edu.pk.jawolh.erecepta.common.visit.proto.GetVisitRequest;
import edu.pk.jawolh.erecepta.common.visit.proto.VisitServiceGrpc;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.service.VisitService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcServerService extends VisitServiceGrpc.VisitServiceImplBase {
    private final VisitService visitService;

    @Override
    public void getVisit(GetVisitRequest request, StreamObserver<GetVisitReply> responseObserver) {
        Optional<Visit> vOpt = visitService.findById(UUID.fromString(request.getVisitId()));

        GetVisitReply.Builder reply = GetVisitReply.newBuilder();

        vOpt.ifPresent(v -> reply.setId(v.getId().toString())
                .setDoctorId(v.getDoctorId().toString())
                .setPatientId(v.getPatientId().toString())
                .setSpecialization(v.getSpecialization().ordinal())
                .setVisitStatus(v.getVisitStatus().ordinal())
                .setVisitTime(v.getVisitTime().toString())
                .build());

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
