package calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    private static void doSum(ManagedChannel channel) {
        System.out.println("enter doSum");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse response = stub.sum(SumRequest.newBuilder().setFirst(1).setSecond(5).build());
        System.out.println("Result: " + response.getResult());
    }

    private static void doPrimes(ManagedChannel channel) {
        System.out.println("enter doPrimes");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.primes(PrimesRequest.newBuilder().setNumber(210).build()).forEachRemaining(
                response -> System.out.println(response.getNumber())
        );
    }

    public static void doAvg(ManagedChannel channel) throws InterruptedException {
        System.out.println("enter doAvg");
        CountDownLatch latch = new CountDownLatch(1);

        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);
        StreamObserver<AvgRequest> stream = stub.avg(new StreamObserver<AvgResponse>() {
            @Override
            public void onNext(AvgResponse response) {
                System.out.println("Average: " + response.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        for (int number : numbers) {
            stream.onNext(AvgRequest.newBuilder().setNumber(number).build());
        }

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doMax(ManagedChannel channel) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        StreamObserver<MaxRequest> stream = stub.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse response) {
                System.out.println("Current Max: " + response.getResult());
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList(1, 5, 2, 10, 2, 21).forEach(number -> stream.onNext(MaxRequest.newBuilder().setNumber(number).build()));
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doSqrt(ManagedChannel channel) {
        System.out.println("Enter doSqrt");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SqrtResponse response = stub.sqrt(SqrtRequest.newBuilder().setNumber(25).build());

        System.out.println("Sqrt 25 = " + response.getResult());

        try {
            response = stub.sqrt(SqrtRequest.newBuilder().setNumber(-1).build());
            System.out.println("Sqrt -1 = " + response.getResult());
        } catch (RuntimeException e) {
            System.out.println("Got an Exception for sqrt");
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws InterruptedException {
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
                break;
            case "avg":
                doAvg(channel);
                break;
            case "max":
                doMax(channel);
                break;
            case "sqrt":
                doSqrt(channel);
                break;
            default:
                System.out.println("Keyword invalid: " + args[0]);
                break;
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
