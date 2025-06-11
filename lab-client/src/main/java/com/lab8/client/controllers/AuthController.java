package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.AuthenticationManager;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Localizator;
import com.lab8.common.util.User;

import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthController {
    private Runnable callback;
    private Localizator localizator;
    private final HashMap<String, Locale> localeMap = new HashMap<>() {{
        put("Русский", new Locale("ru", "RU"));
        put("English(CA)", new Locale("en", "CA"));
        put("Latvian", new Locale("lv"));
        put("Slovenian", new Locale("sl"));
    }};

    @FXML
    private Label titleLabel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button okButton;
    @FXML
    private CheckBox signUpButton;
    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    void initialize() {
        languageComboBox.setItems(FXCollections.observableArrayList(localeMap.keySet()));

        languageComboBox.setValue(SessionHandler.getCurrentLanguage());
        languageComboBox.setStyle("-fx-font: 13px \"Sergoe UI\";");

        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            localizator.setBundle(ResourceBundle.getBundle("locales/gui", localeMap.get(newValue)));
            SessionHandler.setCurrentLanguage(newValue);
            changeLanguage();
        });
    }

    @FXML
    void ok() {
        boolean isRegistration = signUpButton.isSelected();
        authenticate(isRegistration);
    }

    public void authenticate(boolean isRegistration) {
        try {
            if (!validateLogin()) return;
            String Command = isRegistration ? "register" : "login";

            ExecutionResponse<AnswerString> authenticateStatus = (ExecutionResponse<AnswerString>) AuthenticationManager.authenticateUser(loginField.getText(), passwordField.getText(), Command);
            if (authenticateStatus == null) {
                DialogManager.alert("ServerError", localizator);
                return;
            } else if (!authenticateStatus.getExitCode()) {
                DialogManager.alert(authenticateStatus.getAnswer().getAnswer(), localizator);
                return;
            }
            else {
                User user = new User(loginField.getText(), passwordField.getText());
                SessionHandler.setCurrentUser(user);
                SessionHandler.setCurrentLanguage(languageComboBox.getValue());
                DialogManager.info(isRegistration ? "RegisterSuccess" : "LoginSuccess", localizator);
                callback.run();
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateLogin() {
        if (loginField.getText().isEmpty()) {
            DialogManager.alert("LoginFieldError", localizator);
            return false;
        }
        return true;
    }

    public void changeLanguage() {
        titleLabel.setText(localizator.getKeyString("AuthTitle"));
        loginField.setPromptText(localizator.getKeyString("LoginField"));
        passwordField.setPromptText(localizator.getKeyString("PasswordField"));
        signUpButton.setText(localizator.getKeyString("SignUpButton"));
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }
}
