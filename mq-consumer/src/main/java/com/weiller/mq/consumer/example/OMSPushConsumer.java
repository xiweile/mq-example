package com.weiller.mq.consumer.example;

import io.openmessaging.*;
import io.openmessaging.consumer.MessageListener;
import io.openmessaging.consumer.PushConsumer;
import io.openmessaging.exception.OMSResourceNotExistException;

public class OMSPushConsumer {
    public static void main(String[] args) throws OMSResourceNotExistException {
        final MessagingAccessPoint messagingAccessPoint = OMS
            .getMessagingAccessPoint("oms:rocketmq://alice@rocketmq.apache.org/us-east");

        //Fetch a ResourceManager to create Queue resource.
        ResourceManager resourceManager = messagingAccessPoint.resourceManager();
        final PushConsumer consumer = messagingAccessPoint.createPushConsumer();
        consumer.startup();

        //Register a shutdown hook to close the opened endpoints.
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                consumer.shutdown();
                messagingAccessPoint.shutdown();
            }
        }));

        //Consume messages from a simple queue.
        String simpleQueue = "NS://HELLO_QUEUE";
        resourceManager.createQueue( simpleQueue, OMS.newKeyValue());

        //This queue doesn't has a source queue, so only the message delivered to the queue directly can
        //be consumed by this consumer.
        consumer.attachQueue(simpleQueue, new MessageListener() {
            @Override
            public void onReceived(Message message, Context context) {
                System.out.println("Received one message: " + message);
                context.ack();
            }

        });
    }
}