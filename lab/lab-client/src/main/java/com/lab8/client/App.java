package com.lab8.client;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.controllers.AuthController;
import com.lab8.client.managers.ConnectionManager;
import com.lab8.client.util.Console;
import com.lab8.client.util.DefaultConsole;
import com.lab8.common.util.Pair;
import com.lab8.common.validators.ArgumentValidator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import com.lab8.client.util.Localizator;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

public class App extends Application {

    private Stage mainStage;
    private Localizator localizator;
    static Console console = new DefaultConsole();
    private static int attempts = 1;
    private static final int SERVER_PORT = 15719;
    private static final String SERVER_HOST = "localhost";
    private static final ConnectionManager networkManager = new ConnectionManager(SERVER_PORT, SERVER_HOST);
    private static Map<String, Pair<ArgumentValidator, Boolean>> commandsData;
    public static void main(String[] args) {
        try {
            console.println("Запуск клиента...");
            networkManager.connect();
            commandsData = networkManager.receive().getCommandsMap();
            launch();

        } catch (BufferOverflowException | BufferUnderflowException | IOException e) {
            console.printError("Не удалось подключиться к серверу. Проверьте, запущен ли сервер и доступен ли он по адресу " + SERVER_HOST + ":" + SERVER_PORT + " попытка " + attempts);
            console.println(e.getMessage());
            try {
                Thread.sleep(2000);
                attempts++;
            } catch (InterruptedException ignored) {}
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage stage) {
        localizator = new Localizator(ResourceBundle.getBundle("locales/gui", new Locale("en", "IN")));
        SessionHandler.setCurrentLanguage("English(IN)");
        mainStage = stage;
        authStage();
    }


    public void startMain() {
        var mainLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        var mainRoot = loadFxml(mainLoader);


        mainStage.setScene(new Scene(mainRoot));
        mainStage.show();
    }



    private void authStage() {
        var authLoader = new FXMLLoader(getClass().getResource("/auth.fxml"));
        Parent authRoot = loadFxml(authLoader);
        AuthController authController = authLoader.getController();
        authController.setCallback(this::startMain);
        authController.setClient(networkManager);
        authController.setLocalizator(localizator);

        mainStage.setScene(new Scene(authRoot));
        mainStage.setTitle("Products");
        mainStage.setResizable(false);
        mainStage.show();
    }

    private Parent loadFxml(FXMLLoader loader) {
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            console.println("Can't load " + loader.toString() + e);
            System.exit(1);
        }
        return parent;
    }
}