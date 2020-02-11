package com.weiller.mq.base.config;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * rocketMq消息监听配置
 */
@Data
public class RocketMqConfig {
	
	/**
	 * rocketMq服务器地址，集群情况用逗号隔开，例如：10.10.0.221:9876,10.10.0.213:9876
	 */
	@NotNull(message="rocketMq服务器地址不能为空")
	String nameSrvAddr;

	/**
	 * 监听的主题
	 */
	@NotNull(message="监听的主题不能为空")
	String topicName;
	
	/**
	 * 监听的标签，不指定标签用 * 即可
	 */
	@NotNull(message="监听的标签不能为空")
	String tag="*";
	
	/**
	 * 消费者的任务处理线程最小值
	 */
	@Min(value=1,message="消费者的任务处理线程最小值必须大于1")
	int consumeThreadMin=1;
	
	/**
	 * 消费者的任务处理线程最大值
	 */
	@Min(value=1,message="消费者的任务处理线程最大值必须大于1")
	int consumeThreadMax=1;

	/**
	 * 消费者者模式，集群（队列）模式用MessageModel.CLUSTERING，广播（主题）模式用MessageModel.BROADCASTING
	 */
	@NotNull(message="消费者者模式不能为空")
	MessageModel messageModel = MessageModel.CLUSTERING;
    
}
