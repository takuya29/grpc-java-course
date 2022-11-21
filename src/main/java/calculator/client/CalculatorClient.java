package calculator.client;

import com.proto.sum.CalculatorServiceGrpc;
import com.proto.sum.PrimesRequest;
import com.proto.sum.SumRequest;
import com.proto.sum.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    private static void doSum(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse response = stub.sum(SumRequest.newBuilder().setFirst(1).setSecond(5).build());
        System.out.println("Result: " + response.getResult());
    }

    private static void doPrimes(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.primes(PrimesRequest.newBuilder().setNumber(210).build()).forEachRemaining(
                response -> System.out.println(response.getNumber())
        );

    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "sum":
                doSum(channel);
                break;
            case "primes":
                doPrimes(channel);
            default:
                System.out.println("Keyword invalid: " + args[0]);
                break;
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
