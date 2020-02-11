package com.weiller.mq.consumer.listener.impl;

import com.weiller.mq.base.listener.AbstractMessageListener;
import com.weiller.mq.base.config.RocketMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;
/**
 * 数据下发消息监听器
 */
@Component
@Slf4j
public class RocketMqListenerImpl extends AbstractMessageListener {

    @Override
    protected RocketMqConfig getRocketMqConfig() {
        //TODO 这里的配置尽量实时获取，不要用注入方式，以便当配置项修改后，可以实时通知更新
        RocketMqConfig rocketMqConfig = new RocketMqConfig();
        rocketMqConfig.setConsumeThreadMin(1);
        rocketMqConfig.setConsumeThreadMax(2);
        rocketMqConfig.setMessageModel(MessageModel.CLUSTERING);
        rocketMqConfig.setNameSrvAddr("localhost:9876");
        rocketMqConfig.setTag("*");
        rocketMqConfig.setTopicName("TEST");
        return rocketMqConfig;
    }

    @Override
    protected boolean handleMsg(String msgBody , String msgTag){
        log.info("收到消息tag:{},msg:{}",msgTag,msgBody);
        return true;
    }

    @Override
    protected String getListenConfigChangeKey() {
        return "xxxx.".concat(mqCode()).concat(".xxxx"); //配置中心的配置项
    }

}
