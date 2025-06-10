package com.lab8.client.managers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.controllers.MainController;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.Product;
import com.lab8.common.util.Request;
import com.lab8.common.util.Response;

import java.io.IOException;
import java.util.List;

public class UpdatingManager {
    private final MainController mainController;
    private final Localizator localizator;

    public UpdatingManager(MainController mainController, Localizator localizator) {
        this.mainController = mainController;
        this.localizator = localizator;
    }

    private void loadCollection() {
        try {
            ConnectionManager.getInstance().send(new Request("Show", SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive();
            mainController.setCollection((List<Product>) response.getExecutionStatus().getAnswer().getAnswer()); //todo pizdec
            //visualise(true);
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }
}
