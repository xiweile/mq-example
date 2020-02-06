package com.weiller.mq.consumer.example;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
 import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
 import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
 import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.boot.autoconfigure.transaction.TransactionProperties;

import java.util.List;

 public class ScheduledMessageConsumer {

     public static void main(String[] args) throws Exception {
         // Instantiate message consumer
         DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test");
         consumer.setNamesrvAddr("localhost:9876");
         // Subscribe topics
         consumer.subscribe("TestTopic4", "*");
         // Register message listener
         consumer.registerMessageListener(new MessageListenerConcurrently() {
             @Override
             public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                 for (MessageExt message : messages) {
                     // Print approximate delay time period
                     System.out.println("Receive message[msg =" + new String(message.getBody()) + "] "
                             + (System.currentTimeMillis() - message.getStoreTimestamp()) + "ms later");
                 }
                 return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
             }
         });
         // Launch consumer
         consumer.start();
     }
 }