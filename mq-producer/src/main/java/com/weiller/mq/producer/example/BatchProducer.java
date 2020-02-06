package com.weiller.mq.producer.example;

import com.weiller.mq.producer.common.ListSplitter;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量传输消息
 */
public class BatchProducer {
    public static void main(String[] args) throws Exception {
        //实例化一个生产者组name
        DefaultMQProducer producer = new
            DefaultMQProducer("test");
        producer.setNamesrvAddr("127.0.0.1:9876");
        //运行这个实例
        producer.start();
        //创建一个batch消息实例，指定topic，tag和消息体。
        String topic = "BatchTest";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "TagA", "OrderID001", "Hello world 0".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID002", "Hello world 11".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID003", "Hello world 2".getBytes()));
        messages.add(new Message(topic, "TagB", "OrderID005", "Hello world 2".getBytes()));
        messages.add(new Message(topic, "TagC", "OrderID006", "Hello world 2".getBytes()));
        messages.add(new Message(topic, "TagD", "OrderID007", "Hello world 2".getBytes()));
        //then you could split the large list into small ones:
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            try {
                List<Message>  listItem = splitter.next();
                //调用发送消息，投递消息到一个broker.
                SendResult sendResult = producer.send(listItem);
                System.out.printf("%s%n", listItem);
            } catch (Exception e) {
                e.printStackTrace();
                //handle the error
            }
        }
        //当不再使用的时候关闭生产者
        producer.shutdown();
    }
}