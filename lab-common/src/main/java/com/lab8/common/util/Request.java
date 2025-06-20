package com.lab8.common.util;

import com.lab8.common.models.Product;
import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 11L;
    private String string;
    private Product product = null;
    private final User user;

    public Request(String string, Product product, User user) {
        this.string = string;
        this.product = product;
        this.user = user;
    }

    public Request(String string, User user) {
        this.string = string;
        this.user = user;
    }

    public String[] getCommand() {
        String[] inputCommand = (string.trim() + " ").split(" ", 2);
        inputCommand[1] = inputCommand[1].trim();
        return inputCommand;
    }

    public Product getProduct() {
        return product;
    }

    public void setCommand(String command) {
        this.string = command;
    }

    public User getUser() {
        return user;
    }

    public void setBand(Product band) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Request{" +
                "string='" + string + '\'' +
                ", product=" + product +
                '}';
    }
}