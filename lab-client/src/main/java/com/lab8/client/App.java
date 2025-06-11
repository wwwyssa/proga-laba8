package com.lab8.client;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.controllers.AuthController;
import com.lab8.client.controllers.EditController;
import com.lab8.client.controllers.MainController;
import com.lab8.client.managers.ConnectionManager;
import com.lab8.client.util.Console;
import com.lab8.client.util.DefaultConsole;
import com.lab8.client.util.Localizator;
import com.lab8.common.util.Pair;
import com.lab8.common.validators.ArgumentValidator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

// Вариант 9886
public class App extends Application {

    private Stage mainStage;
    private Localizator localizator;
    static Console console = new DefaultConsole();
    private static int attempts = 1;
    private static final ConnectionManager networkManager = ConnectionManager.getInstance();
    private static Map<String, Pair<ArgumentValidator, Boolean>> commandsData;
    public static void main(String[] args) {
        console.println("Запуск клиента..."); //fixme клиент не может переподключиться после открытия окна
        do {
            try {
                networkManager.connect();
                attempts = 1;

                launch();
            } catch (IOException e) {
                console.printError("Не удалось подключиться к серверу. Проверьте, запущен ли сервер и доступен ли он по адресу " + networkManager.getPort() + ":" + networkManager.getHost() + " попытка " + attempts);
                try {
                    Thread.sleep(2000);
                    attempts++;
                } catch (InterruptedException ignored) {}
            }
        } while (attempts < 5);
        System.exit(0);
    }

    @Override
    public void start(Stage stage) {
        localizator = new Localizator(ResourceBundle.getBundle("locales/gui", new Locale("en", "CA")));
        SessionHandler.setCurrentLanguage("English(CA)");
        mainStage = stage;
        authStage();
    }

    public EditController createEditController() {
        FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/edit.fxml"));
        Parent editRoot = loadFxml(editLoader);

        Scene editScene = new Scene(editRoot);
        Stage editStage = new Stage();
        editStage.setScene(editScene);
        editStage.setResizable(false);
        editStage.setTitle("Edit Product");
        EditController editController = editLoader.getController();

        editController.setStage(editStage);
        editController.setLocalizator(localizator);
        return editController;
    }

    public void startMain() {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent mainRoot = loadFxml(mainLoader);
        MainController mainController = mainLoader.getController();
        mainController.setLocalizator(localizator);
        mainController.setEditController(createEditController());

        mainStage.setScene(new Scene(mainRoot));
        mainStage.setTitle("Lab 8 Client");
        mainStage.show();
    }

    private void authStage() {
        FXMLLoader authLoader = new FXMLLoader(getClass().getResource("/auth.fxml"));
        Parent authRoot = loadFxml(authLoader);
        AuthController authController = authLoader.getController();
        authController.setCallback(this::startMain);
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
            console.printError("Can't load " + loader + e);
            System.exit(1);
        }
        return parent;
    }
}