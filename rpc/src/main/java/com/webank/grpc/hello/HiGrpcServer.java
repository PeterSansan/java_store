/**
 * GRPC-JAVA DEMO
 */
package com.webank.grpc.hello;

import java.io.InterruptedIOException;
import java.util.logging.Logger;

import com.webank.grpc.GreeterGrpc;
import com.webank.grpc.HelloReply;
import com.webank.grpc.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.io.IOException;


public class HiGrpcServer {
    private static final Logger logger = Logger.getLogger(HiGrpcServer.class.getName());
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("GRPC SERVER SETUP.");
        final HiGrpcServer server = new HiGrpcServer();
        server.start();
        server.blockUntilShutdown();

    }

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        // when jvm close,close all server
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                        System.err.println("*** shutting down gRPC server since JVM is shutting down");
                        HiGrpcServer.this.stop();
                        System.err.println("*** server shut down ***");
                    }
                }
        );
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * rewrite server call method
     */
    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        static int callNum = 0;

        @Override
        public void call(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();

            printCallNum();

            // set return message
            responseObserver.onNext(reply);
            // complete caller
            responseObserver.onCompleted();
        }

        private void printCallNum() {
            callNum += 1;
            logger.info("No " + callNum + " call");
        }
    }


}
