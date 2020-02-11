package com.weiller.mq.base.listener;

import com.weiller.mq.base.config.RocketMqConfig;
import com.weiller.mq.base.constants.MessageListenerType;
import com.weiller.mq.base.exception.MqRetryException;
import com.weiller.mq.base.service.IMessageListenerType;
import com.weiller.mq.base.utils.StringUtil;
import com.weiller.mq.base.utils.Validators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 公共消息监听抽象类
 */
@Slf4j
public abstract class AbstractMessageListener implements MessageListenerConcurrently {


    @Autowired
    protected IMessageListenerType currentMessageListenerType;

    private DefaultMQPushConsumer rocketMqConsumer;

    /**
     * 计数器
     */
    protected volatile AtomicInteger mqConsumerCount = new AtomicInteger();
    protected volatile AtomicInteger mqConsumerCountSucess = new AtomicInteger();
    protected volatile AtomicInteger mqConsumerCountFail = new AtomicInteger();

    /**
     * 消息处理
     *
     * @return
     */
    protected abstract boolean handleMsg(String msgBodyObject, String msgTag);

    /**
     * 设置监听器配置
     */
    protected abstract RocketMqConfig getRocketMqConfig();

    /**
     * 当该配置项发生变更后重新加载本监听器
     */
    protected abstract String getListenConfigChangeKey();


    @PostConstruct
    public void init(){
        RocketMqConfig rocketMqConfig = getRocketMqConfig();
        if (rocketMqConfig == null) {
            throw new RuntimeException("未设置rocketMqConfig，无法初始化消息监听器");
        }

        log.info("rocketMqConfig:{}", rocketMqConfig);

        if (MessageListenerType.RocketMQ.equals(currentMessageListenerType.getCurrentMessageListenerType())) {
            initRocketMqMessageListener(rocketMqConfig);
        }


        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("mqConsumerCount-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(() -> {
            int successCount = mqConsumerCountSucess.getAndSet(0);
            log.warn("1分钟 mq consumer:{},sucess:{},fail:{},tps:{}/s", mqConsumerCount.getAndSet(0), successCount, mqConsumerCountFail.getAndSet(0), successCount / 60);
        } , 30, 60, TimeUnit.SECONDS);

    }

    @PreDestroy
    public void destroy() {
        if (MessageListenerType.ONS.equals(currentMessageListenerType.getCurrentMessageListenerType())) {
            rocketMqConsumer.shutdown();
        }
    }

    public void initRocketMqMessageListener(RocketMqConfig rocketMqConfig) {
        rocketMqConsumer = new DefaultMQPushConsumer("default");
        rocketMqConsumer.setNamesrvAddr(rocketMqConfig.getNameSrvAddr());
        try {
            // 订阅Topic下的消息
            rocketMqConsumer.subscribe(rocketMqConfig.getTopicName(), rocketMqConfig.getTag());
            rocketMqConsumer.setConsumeThreadMin(rocketMqConfig.getConsumeThreadMin());
            rocketMqConsumer.setConsumeThreadMax(rocketMqConfig.getConsumeThreadMax());
            // 消息监听模式
            rocketMqConsumer.setMessageModel(rocketMqConfig.getMessageModel());
            /**
             * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
             * 如果非第一次启动，那么按照上次消费的位置继续消费
             */
            rocketMqConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            rocketMqConsumer.registerMessageListener(this);
            rocketMqConsumer.start();
            log.info("rocketMQ消息监听器启动成功！");
        } catch (Exception e) {
            log.debug("rocketMQ消息监听器启动失败！", e);
        }
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        mqConsumerCount.incrementAndGet();
        for (MessageExt msg : msgs) {
            try {
                String msgData = StringUtil.byteToString(msg.getBody());
                if (!this.handleMsg(msgData, msg.getTags())) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                mqConsumerCountSucess.incrementAndGet();
            } catch (Exception e) {
                mqConsumerCountFail.incrementAndGet();
                //记录异常日志
                log.error("消息消费异常", e);
                //如果该异常判定为需要重新消费该消息，则把消息回退，下次继续消费
                if (this.isRetryException(e)) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

    }

    /**
     * 刷新监听器配置更新
     */
    public boolean reloadIfNecessary(String changeConfigKey) {
        if (this.getListenConfigChangeKey() != null && changeConfigKey.startsWith(getListenConfigChangeKey())) {
            log.debug("开始重新配置消息监听器：" + this.getRocketMqConfig().getTopicName());
            //先检查新配置有没有问题
            RocketMqConfig rocketMqConfig = getRocketMqConfig();
            if (rocketMqConfig == null) {
                throw new RuntimeException("未设置rocketMqConfig，无法初始化消息监听器");
            }
            Validators.validate(rocketMqConfig);
            //没问题再shutdown之前的
            destroy();
            //然后再重新实例化
            init();
            log.debug("重新配置消息监听器" + this.getRocketMqConfig().getTopicName() + "完成");
            return true;
        }
        return false;
    }


    /**
     * 判断当前异常是否需要回退消息，下次继续消费
     *
     * @param e
     * @return
     */
    protected boolean isRetryException(Exception e) {
        boolean isRetry = false;
        if (e instanceof MqRetryException) {
            isRetry = true;
        } else if (e.getMessage() != null && (e.getMessage().indexOf("ClientException") != -1 || e.getMessage().indexOf("MqRetryException") != -1)) {
            isRetry = true;
        }
        return isRetry;
    }

    protected String mqCode() {
        return this.currentMessageListenerType.getCurrentMessageListenerType().getCode();
    }
}
