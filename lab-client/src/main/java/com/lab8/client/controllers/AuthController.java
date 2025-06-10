package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.AuthenticationManager;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Localizator;
import com.lab8.client.util.exceptions.UserAlreadyExistsException;
import com.lab8.common.util.User;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        loginField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches(".{0,40}")) {
                loginField.setText(oldValue);
            }
        });
        passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\S*")) { // todo чем не понравились пробелы в пароле
                passwordField.setText(oldValue);
            }
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
            User user = AuthenticationManager.authenticateUser(loginField.getText(), passwordField.getText(), Command);

            if (user == null) {
                throw new UserAlreadyExistsException();
            }

            SessionHandler.setCurrentUser(user);
            SessionHandler.setCurrentLanguage(languageComboBox.getValue());
            DialogManager.info(isRegistration ? "RegisterSuccess" : "LoginSuccess", localizator);
            callback.run();

        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        } catch (UserAlreadyExistsException e) {
            DialogManager.alert(isRegistration ? "UserAlreadyExists" : "UserNotFound", localizator);
        }
    }


    private boolean validateLogin() {
        if (loginField.getText().isEmpty() || loginField.getText().length() > 40) {
            DialogManager.alert("LoginFieldError", localizator); // todo добавить в локализацию
            return false;
        }
        return true;
    }

    private List<String> validatePassword(String password) { //todo я считаю нам это нафиг не надо, это для крутых
        var errors = new ArrayList<String>();
        if (password.length() < 8) {
            errors.add(localizator.getKeyString("PasswordMin6"));
        }

        int upChars = 0, lowChars = 0, digits = 0, special = 0;
        for(var i = 0; i < password.length(); i++) {
            var ch = password.charAt(i);
            if (Character.isUpperCase(ch)) {
                upChars++;
            } else if(Character.isLowerCase(ch)) {
                lowChars++;
            } else if(Character.isDigit(ch)) {
                digits++;
            } else {
                special++;
            }
        }

        if (upChars == 0) errors.add(localizator.getKeyString("PasswordContainUp"));
        if (lowChars == 0) errors.add(localizator.getKeyString("PasswordContainLow"));
        if (digits == 0) errors.add(localizator.getKeyString("PasswordContainDigit"));
        if (special == 0) errors.add(localizator.getKeyString("PasswordContainSpecial"));

        return errors;
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
