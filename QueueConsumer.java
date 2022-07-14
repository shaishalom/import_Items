package com.example.transactionaccessor.consumer;


import com.example.transactionaccessor.model.TempObject;
import com.example.transactionaccessor.producer.QueueProducer;
import com.example.transactionaccessor.service.DataProcessingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueueConsumer {

    @Autowired
    QueueProducer producer;
    private DataProcessingService service = new DataProcessingService();
    List<TempObject> listOfObjects;

    @RabbitListener(queues = {"${accessorQueue.name}"})
    public void receiveCommand(@Payload String data){
        try {

            listOfObjects = service.processingData(data);
            if (listOfObjects.size() == 1) {
                for (TempObject object : listOfObjects) {
                    producer.sendResult("OK - Transaction Accessor");
                }
            } else {
                // else throw answer with exception to Queue
                producer.sendResult("Error - Transaction Accessor");
            }
        } catch (Exception ex){
            //producer.sendResult("Error with sending data to TP Transaction Accessor");
        }

    }
}
