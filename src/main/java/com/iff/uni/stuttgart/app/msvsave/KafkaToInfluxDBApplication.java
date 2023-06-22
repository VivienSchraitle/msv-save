package com.iff.uni.stuttgart.app.msvsave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableKafka 
@EnableEurekaClient

public class KafkaToInfluxDBApplication{

	public static void main(String[] args) {
		SpringApplication.run(KafkaToInfluxDBApplication.class, args);
	}

}
