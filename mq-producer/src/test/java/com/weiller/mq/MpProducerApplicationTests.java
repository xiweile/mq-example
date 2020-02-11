package com.weiller.mq;

import com.weiller.mq.base.service.IMsgProducer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MpProducerApplicationTests {

	@Autowired
	@Qualifier(value = "msgProducer")
	IMsgProducer msgProducer;

	@Test
	void sendMsgTest() {
		boolean b = msgProducer.sendMsg("TEST", "TAG1","HELLO MY MESSAGE!!");
	}

}
