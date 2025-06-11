package com.lab8.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class IdInputController {

    @FXML
    private TextField numberInput;

    private Stage stage;

    @FXML
    private void confirmInput() {
        try {
            int number = Integer.parseInt(numberInput.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Input Confirmed");
            alert.setHeaderText(null);
            alert.setContentText("You entered: " + number);
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid number.");
            alert.showAndWait();
        }
    }

    public void show() {
        if (!stage.isShowing()) {
            stage.showAndWait();
        }
    }

    public void clear() {
        numberInput.clear();
    }

    public int getId() {
        try {
            return Integer.parseInt(numberInput.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID entered.");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}