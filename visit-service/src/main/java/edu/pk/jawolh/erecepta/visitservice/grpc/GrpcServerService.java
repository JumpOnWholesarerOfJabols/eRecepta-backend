package edu.pk.jawolh.erecepta.visitservice.grpc;

import edu.pk.jawolh.erecepta.common.visit.proto.CancelVisitByUserIdReply;
import edu.pk.jawolh.erecepta.common.visit.proto.CancelVisitByUserIdRequest;
import edu.pk.jawolh.erecepta.common.visit.proto.VisitServiceGrpc;
import edu.pk.jawolh.erecepta.visitservice.service.VisitService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcServerService extends VisitServiceGrpc.VisitServiceImplBase {
    private final VisitService visitService;

    @Override
    public void cancelVisitByUserId(CancelVisitByUserIdRequest request, StreamObserver<CancelVisitByUserIdReply> responseObserver) {
        log.info("Received cancelVisit request, userId={}", request.getUserId());

        CancelVisitByUserIdReply.Builder reply = CancelVisitByUserIdReply.newBuilder();
        UUID id;

        try {
            id = UUID.fromString(request.getUserId());
            reply.setSuccess(visitService.cancelVisitByUserId(id));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", request.getUserId(), e);
            reply.setSuccess(false);
        } catch (Exception e) {
            log.error("Unexpected error while cancelling visits: {}", request.getUserId(), e);
            reply.setSuccess(false);
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
