package com.example.transactionaccessor.service;

import com.example.transactionaccessor.model.Status;
import com.example.transactionaccessor.model.TempObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataProcessingService {

    List<String> requestDataAddresses = new ArrayList<>();
    List<TempObject> listObjects;

    HttpClient client = HttpClient.newHttpClient();

    public DataProcessingService(){
        requestDataAddresses.add("https://www.google.com/");
/*        requestDataAddresses.add("https://www.ikea.com/");
        requestDataAddresses.add("https://www.netflix.com/il-en/");
        requestDataAddresses.add("https://stackoverflow.com/");
        requestDataAddresses.add("https://www.youtube.com/");
        requestDataAddresses.add("https://spring.io/");
        requestDataAddresses.add("https://www.instagram.com/");
        requestDataAddresses.add("https://www.facebook.com");*/
    }

    public List<TempObject> processingData(String dataFromQueue){
        try {
            listObjects = new ArrayList<>();
            Random ran = new Random();
            for (String data : requestDataAddresses) {
                Status result = sendingRequest(data);
                if (result==Status.ERROR) {
                    return listObjects;
                }
                TempObject object = new TempObject(dataFromQueue, data, ran.nextInt(100), Math.abs(ran.nextInt()));
                listObjects.add(object);
            }
            return listObjects;
        } catch (Exception ex){
            ex.printStackTrace();
            return listObjects;
        }

    }

    public Status sendingRequest(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(uri))
                .header("accept", "application/json")
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if ((response.statusCode()==200)||(response.statusCode()==201)) return Status.FINISHED;
        return Status.ERROR;
    }

}
