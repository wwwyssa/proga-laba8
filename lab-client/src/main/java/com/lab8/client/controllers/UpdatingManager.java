package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.ConnectionManager;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.Product;
import com.lab8.common.util.Request;
import com.lab8.common.util.Response;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;

public class UpdatingManager {
    private final MainController mainController;
    private final Localizator localizator;
    private volatile boolean isRefreshing;

    public UpdatingManager(MainController mainController) {
        this.mainController = mainController;
        this.localizator = mainController.getLocalizator();
    }

    public void stopRefreshing() {
        this.isRefreshing = false;
    }

    public void refresh() {
        this.isRefreshing = true;
        Thread thread = new Thread(() -> {
            while (isRefreshing) {
                Platform.runLater(this::loadCollection);
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                }
            }
        });
        thread.start();
    }

    public void loadCollection() {
        try {
            ConnectionManager.getInstance().send(new Request("show", SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive();
            mainController.setCollection((List<Product>) response.getExecutionStatus().getAnswer().getAnswer()); //todo pizdec
            //visualise(true);
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }
}
