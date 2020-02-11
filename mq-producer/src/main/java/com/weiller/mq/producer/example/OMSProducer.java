package com.weiller.mq.producer.example;

import io.openmessaging.*;
import io.openmessaging.producer.Producer;
import io.openmessaging.producer.SendResult;

import java.nio.charset.Charset;

public class OMSProducer {
    public static void main(String[] args) {
        final MessagingAccessPoint messagingAccessPoint = OMS
            .getMessagingAccessPoint("oms:rocketmq://localhost:9876/namespace");
        final Producer producer = messagingAccessPoint.createProducer();
        messagingAccessPoint.startup();
        producer.startup();

        //Register a shutdown hook to close the opened endpoints.
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                producer.shutdown();
                messagingAccessPoint.shutdown();
            }
        }));

        //Sends a message to the specified destination synchronously.
        {
            SendResult sendResult = producer.send(producer.createBytesMessage(
                    "NS://HELLO_QUEUE", "HELLO_BODY".getBytes(Charset.forName("UTF-8"))));

            System.out.println("Send sync message OK, message id is: " + sendResult.messageId());
        }

        //Sends a message to the specified destination asynchronously.
        //And get the result through Future
        {
            final Future<SendResult> result = producer.sendAsync(producer.createBytesMessage(
                    "NS://HELLO_QUEUE", "HELLO_BODY".getBytes(Charset.forName("UTF-8"))));

            final SendResult sendResult = result.get(3000L);
            System.out.println("Send async message OK, message id is: " + sendResult.messageId());
        }

        //Sends a message to the specified destination asynchronously.
        //And retrieve the result through FutureListener
        {
            final Future<SendResult> result = producer.sendAsync(producer.createBytesMessage(
                    "NS://HELLO_QUEUE", "HELLO_BODY".getBytes(Charset.forName("UTF-8"))));

            result.addListener(new FutureListener<SendResult>() {

                @Override
                public void operationComplete(Future<SendResult> future) {
                    if (future.isDone() && null == future.getThrowable()) {
                        System.out.println("Send async message OK, message id is: " + future.get().messageId());
                    } else {
                        System.out.println("Send async message Failed, cause is: " + future.getThrowable().getMessage());
                    }
                }
            });
        }

        //Sends a message to the specific queue in OneWay manner.
        {
            //There is no {@code Future} related or {@code RuntimeException} thrown. The calling thread doesn't
            //care about the send result and also have no context to get the result.
            producer.sendOneway(producer.createBytesMessage(
                    "NS://HELLO_QUEUE", "HELLO_BODY".getBytes(Charset.forName("UTF-8"))));
        }
    }
}