package com.example.transactionaccessor.model;

import lombok.Data;

@Data
public class TempObject {

    private String name;
    private String address;
    private int sum;
    private int cardNumber;

    public TempObject(){

    }

    public TempObject(String name, String address, int sum, int cardNumber) {
        this.name = name;
        this.address = address;
        this.sum = sum;
        this.cardNumber = cardNumber;
    }

}
