/**
 * 
 */
package com.weiller.mq.base.service.impl;

import com.weiller.mq.base.constants.RocketMqConstants;
import com.weiller.mq.base.service.IMsgProducer;
import com.weiller.mq.base.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * ONS消息发送器，需要使用的应用自己声明实例化该类，这里不做自动实例化
 */
@Slf4j
public class RocketMqMsgProducer implements IMsgProducer {

	/**
	 * rocketMq服务器地址，集群情况用逗号隔开，例如
	 * 10.10.0.221:9876,10.10.0.213:9876
	 */
	String namesrvAddr;
	Map<String, DefaultMQProducer> producers = new HashMap<String, DefaultMQProducer>();
	
	public RocketMqMsgProducer(String namesrvAddr){
		this.namesrvAddr = namesrvAddr;
	}
	
	private DefaultMQProducer getProducer(String topic){
		if(producers.get(topic) == null){
			synchronized (topic) {
				if(producers.get(topic) == null){
					DefaultMQProducer producer = new DefaultMQProducer("default");
					producer.setNamesrvAddr(namesrvAddr);
					try {
						producer.start();
						producers.put(topic, producer);
					} catch (MQClientException e) {
						log.debug("rocketMQ消息发送器初始化失败！",e);
					}
				}
			}
		}
		return producers.get(topic);
	}
	
	@PreDestroy
	public void destory(){
		for(DefaultMQProducer producer : producers.values()){
			producer.shutdown();
		}
	}
	
	@Override
	public boolean sendMsg(String topic, String tags, String keys, String msg , int delayLevel) {
        try {
			Message sendMsg = new Message(topic, tags, keys, StringUtil.toByteArray(msg));
			if(delayLevel != 0){
				sendMsg.setDelayTimeLevel(delayLevel);
			}
        	getProducer(topic).send(sendMsg);
			return true;
		}catch (UnsupportedEncodingException e){
			log.debug("rocketMQ消息内容异常！",e);
			return false;
		} catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
			log.debug("rocketMQ消息发送失败！",e);
			return false;
		}
	}

	@Override
	public boolean sendMsg(String topic, String tags, String msg , int delayLevel) {
		return this.sendMsg(topic, tags, RocketMqConstants.MSG_DEFAULT_KEY, msg,delayLevel);
	}

	@Override
	public boolean sendMsg(String topic, String msg , int delayLevel) {
		return this.sendMsg(topic, RocketMqConstants.MSG_DEFAULT_TAGS, RocketMqConstants.MSG_DEFAULT_KEY, msg,delayLevel);
	}

	@Override
	public boolean sendMsg(String topic, String tags,String msg) {
		return this.sendMsg(topic,  tags,msg,0);
	}

}
