package calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        responseObserver.onNext(SumResponse.newBuilder().setResult(request.getFirst() + request.getSecond()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void primes(PrimesRequest request, StreamObserver<PrimesResponse> responseObserver) {
        int k = 2;
        int N = request.getNumber();
        while (N > 1) {
            if (N % k == 0) {
                responseObserver.onNext(PrimesResponse.newBuilder().setNumber(k).build());
                N = N / k;
            } else {
                ++k;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AvgRequest> avg(StreamObserver<AvgResponse> responseObserver) {
        return new StreamObserver<AvgRequest>() {
            private int sum = 0;
            private int size = 0;

            @Override
            public void onNext(AvgRequest request) {
                sum += request.getNumber();
                ++size;
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(AvgResponse.newBuilder().setResult((double) sum / size).build());
                responseObserver.onCompleted();
            }
        };
    }
}
