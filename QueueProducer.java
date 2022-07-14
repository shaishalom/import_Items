package com.example.transactionaccessor.producer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue statusQueue;

    public void sendResult(String data){
        rabbitTemplate.convertAndSend(this.statusQueue.getName(), data);
    }

}
