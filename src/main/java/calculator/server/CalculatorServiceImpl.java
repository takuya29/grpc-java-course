package calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
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

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<MaxRequest>() {
            private int max = Integer.MIN_VALUE;

            @Override
            public void onNext(MaxRequest request) {
                max = Math.max(max, request.getNumber());
                responseObserver.onNext(MaxResponse.newBuilder().setResult(max).build());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void sqrt(SqrtRequest request, StreamObserver<SqrtResponse> responseObserver) {
        int number = request.getNumber();

        if (number < 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The number being set cannot be negative")
                    .augmentDescription("Number: " + number)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(SqrtResponse.newBuilder().setResult(Math.sqrt((double) number)).build());
        responseObserver.onCompleted();


    }
}
