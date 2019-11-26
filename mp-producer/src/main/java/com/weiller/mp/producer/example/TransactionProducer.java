package com.weiller.mp.producer.example;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 事务性生产者
 */
public class TransactionProducer {

    public static void main(String[] args) throws Exception{
        //Instantiate with a producer group name.
        TransactionMQProducer producer = new TransactionMQProducer("test");
        // Specify name server addresses.
        producer.setNamesrvAddr("localhost:9876");
        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        // 事务监听器
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                return null;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                return null;
            }
        });
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 5; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message(" TransactionTopic" /* Topic */,
                    tags[i] /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            producer.sendMessageInTransaction(msg,null);

        }
        producer.shutdown();
    }
}
