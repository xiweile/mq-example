package com.weiller.mq.base.service;

/**
 * 消息发送器
 */
public interface IMsgProducer {

	/**
	 * 发送消息到rocketMQ
	 * @param topic
	 * @param tags
	 * @param keys
	 * @param msg
	 * @return
	 */
	boolean sendMsg(String topic, String tags, String keys, String msg ,int delayLevel );

	/**
	 * 发送消息到rocketMQ
	 * @param topic
	 * @param tags
	 * @param msg
	 * @return
	 */
	boolean sendMsg(String topic, String tags, String msg ,int delayLevel );
	
	/**
	 * 发送消息到rocketMQ
	 * @param topic
	 * @param msg
	 * @return
	 */
	boolean sendMsg(String topic, String msg ,int delayLevel );


	/**
	 * 无延迟，发送消息到rocketMQ
	 * @param topic
	 * @param msg
	 * @return
	 */
	boolean sendMsg(String topic,String tags,  String msg  );
}
