package calculator.server;

import com.proto.sum.SumRequest;
import com.proto.sum.SumResponse;
import com.proto.sum.SumServiceGrpc;
import io.grpc.stub.StreamObserver;

public class SumServiceImpl extends SumServiceGrpc.SumServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        responseObserver.onNext(SumResponse.newBuilder().setResult(request.getFirst() + request.getSecond()).build());
        responseObserver.onCompleted();
    }
}
