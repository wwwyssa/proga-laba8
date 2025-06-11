package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.ConnectionManager;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.Product;
import com.lab8.common.util.Request;
import com.lab8.common.util.Response;
import com.lab8.common.util.executions.ListAnswer;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public class UpdatingManager {
    private final MainController mainController;
    private Localizator localizator;
    private volatile boolean isRefreshing;

    public UpdatingManager(MainController mainController) {
        this.mainController = mainController;
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
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
            if (response.getExecutionStatus().getAnswer() instanceof ListAnswer) {
                mainController.setCollection((List<Product>) response.getExecutionStatus().getAnswer().getAnswer());
            }
            else {
                DialogManager.alert("InvalidResponse", localizator);
            }
        } catch (ClassNotFoundException | IOException e) {
            stopRefreshing();
            for (int i = 1; i <= 5; i++) {
                try {
                    ConnectionManager.getInstance().connect();
                    DialogManager.info("ConnectionEstablished", localizator);
                    refresh();
                    break;
                } catch (IOException ex) {
                    DialogManager.createAlert(localizator.getKeyString("Error"), MessageFormat.format(localizator.getKeyString("ReconnectingAttempt"), i, 5), Alert.AlertType.ERROR, false);
                    if (i == 5) {
                        DialogManager.alert("Sosi", localizator);
                        mainController.logout();
                    }
                }
            }
        }
    }
}