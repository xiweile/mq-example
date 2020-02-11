package com.weiller.mq.producer.example;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 可靠同步传输
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception {
        //实例化一个生产者组name
        DefaultMQProducer producer = new
            DefaultMQProducer("default222");
        producer.setNamesrvAddr("127.0.0.1:9876");
        //运行这个实例
        producer.start();
        for (int i = 0; i < 10; i++) {
            //创建一个消息实例，指定topic，tag和消息体。
            Message msg = new Message("TEST" /* Topic */,
                "TagA" /* Tag */,
                ("Hello RocketMQ " +
                    i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //调用发送消息，投递消息到一个broker.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        //当不再使用的时候关闭生产者
        producer.shutdown();
    }
}