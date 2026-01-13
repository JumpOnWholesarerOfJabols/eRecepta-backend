package edu.pk.jawolh.erecepta.adminservice.client;

import edu.pk.jawolh.erecepta.common.visit.proto.CancelVisitByUserIdReply;
import edu.pk.jawolh.erecepta.common.visit.proto.CancelVisitByUserIdRequest;
import edu.pk.jawolh.erecepta.common.visit.proto.VisitServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcVisitClient {
    private final VisitServiceGrpc.VisitServiceBlockingStub grpcVisitServiceStub;

    public boolean cancelVisitsByUserId(String userId) {
        CancelVisitByUserIdReply r = grpcVisitServiceStub.cancelVisitByUserId(
                        CancelVisitByUserIdRequest.newBuilder()
                                .setUserId(userId)
                                .build());
        return r.getSuccess();
    }
}
