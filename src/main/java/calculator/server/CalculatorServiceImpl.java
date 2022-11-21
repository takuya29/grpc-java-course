package calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

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
        List<Integer> numbers = new ArrayList<>();

        return new StreamObserver<AvgRequest>() {
            @Override
            public void onNext(AvgRequest request) {
                numbers.add(request.getNumber());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                double sum = 0;
                for (int number : numbers) {
                    sum += number;
                }
                responseObserver.onNext(AvgResponse.newBuilder().setResult(sum / numbers.size()).build());
                responseObserver.onCompleted();
            }
        };
    }
}
