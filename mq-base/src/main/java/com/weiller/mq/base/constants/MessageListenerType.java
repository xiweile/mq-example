package com.weiller.mq.base.constants;

public enum MessageListenerType {

	RocketMQ("rocketMq"),
	ONS("ons");
	
	private String code;
	MessageListenerType(String code){
		this.code = code;
	}
	public String getCode() {
		return code;
	}
	
}
