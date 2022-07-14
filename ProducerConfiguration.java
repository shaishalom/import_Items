package com.example.transactionaccessor.producer;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class ProducerConfiguration {
    @Value("${statusQueue.name}")
    private String data;

    @Bean
    public Queue queue(){
        return new Queue(data, true);
    }
}
