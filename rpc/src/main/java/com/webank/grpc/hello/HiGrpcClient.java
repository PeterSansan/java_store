package com.webank.grpc.hello;

import com.webank.grpc.GreeterGrpc;
import com.webank.grpc.HelloReply;
import com.webank.grpc.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiGrpcClient {
    private static final Logger logger = Logger.getLogger(HiGrpcClient.class.getName());
    private ManagedChannel channel;
    private GreeterGrpc.GreeterBlockingStub blockingStub;
    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception{
        HiGrpcClient client = new HiGrpcClient("localhost",50051);
        try{
            String user = "world";
            // Use the arg as the name to greet if provided
            if (args.length > 0){
                user = args[0];
            }
            client.greet(user);
        } finally {
//            client.shutdown();
        }
    }
    /** Construct client for accessing HelloWorld server using the existing channel. */
    private HiGrpcClient(ManagedChannel channel) {
        this.channel = channel;
        // instance
        this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public HiGrpcClient(String host,int port){
        this(ManagedChannelBuilder.forAddress(host,port)
        .usePlaintext()
        .build());
    }

    public void shutdown() throws InterruptedException{
        channel.shutdown().awaitTermination(5,TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void greet(String name){
        logger.info("Will try to greet "+ name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try{
            response = blockingStub.call(request);
        } catch (StatusRuntimeException e){
            logger.log(Level.WARNING,"RPC failed:{0}",e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }




}
