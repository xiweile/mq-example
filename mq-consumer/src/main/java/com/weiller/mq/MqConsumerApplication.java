package com.weiller.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MqConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqConsumerApplication.class, args);
	}

}
