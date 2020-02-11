package com.weiller.mq.base.config;

import com.weiller.mq.base.constants.MessageListenerType;
import com.weiller.mq.base.service.IMessageListenerType;
import com.weiller.mq.base.service.IMsgProducer;
import com.weiller.mq.base.service.impl.RocketMqMsgProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnExpression("'${deployment.codition.mq}'.equals('rkmq')")
public class RocketMQAutoConfiguration {

	@Bean("msgProducer")
    public IMsgProducer rocketMqMsgProducer(){
    	String rocketMqNamesrvAddr = "localhost:9876";
    	if(rocketMqNamesrvAddr == null){
    		throw new RuntimeException("找不到配置项nameSrvAddr的值，无法初始化rocketMqMsgProducer");
    	}
    	return new RocketMqMsgProducer(rocketMqNamesrvAddr);
    } 

	@Bean
	public IMessageListenerType messageListenerType(){
		return new IMessageListenerType() {
			
			@Override
			public MessageListenerType getCurrentMessageListenerType() {
				return MessageListenerType.RocketMQ;
			}
		};
	}
}
