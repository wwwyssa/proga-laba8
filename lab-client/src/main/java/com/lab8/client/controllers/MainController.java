package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.ConnectionManager;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Console;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.Product;
import com.lab8.common.util.Request;
import com.lab8.common.util.Response;

import com.lab8.common.util.ValidAnswer;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.util.executions.ListAnswer;
import javafx.application.Platform;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {
    private Localizator localizator;

    private Runnable authCallback;
    private volatile boolean isRefreshing = true;

    private List<Product> collection;

    private final HashMap<String, Locale> localeMap = new HashMap<>() {{
        put("Русский", new Locale("ru", "RU"));
        put("English(CA)", new Locale("en", "CA"));
        put("Latvian", new Locale("lv"));
        put("Slovenian", new Locale("sl"));
    }};

    private HashMap<String, Color> colorMap;
    private HashMap<Integer, Label> infoMap;
    private Random random;

    private EditController editController;
    private Stage stage;

    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private Label userLabel;
    @FXML
    private Button helpButton;
    @FXML
    private Button infoButton;
    @FXML
    private Button averageOfManufactureCostButton;
    @FXML
    private Button minByNameButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button printFieldAscendingPartNumberButton;
    @FXML
    private Button removeGreaterKeyButton;
    @FXML
    private Button addButton;
    @FXML
    private Button removeByIdButton;
    @FXML
    private Button removeGreaterButton;
    @FXML
    private Button executeScriptButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button logoutButton;

    @FXML
    private Tab tableTab;
    @FXML
    private TableView<Product> tableTable;
    @FXML
    private TableColumn<Product, Long> idColumn;
    @FXML
    private TableColumn<Product, String> ownerColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Integer> coordinatesXColumn;
    @FXML
    private TableColumn<Product, Integer> coordinatesYColumn;
    @FXML
    private TableColumn<Product, String> creationDateColumn;
    @FXML
    private TableColumn<Product, Integer> priceColumn;
    @FXML
    private TableColumn<Product, String> partNumberColumn;
    @FXML
    private TableColumn<Product, Integer> manufactureCostColumn;
    @FXML
    private TableColumn<Product, String> unitOfMeasureColumn;
    @FXML
    private TableColumn<Product, Long> manufacturerIdColumn;
    @FXML
    private TableColumn<Product, String> manufacturerNameColumn;
    @FXML
    private TableColumn<Product, Integer> manufacturerEmployeesCountColumn;
    @FXML
    private TableColumn<Product, String> manufacturerTypeColumn;
    @FXML
    private TableColumn<Product, String> manufacturerOfficialAddressStreetColumn;
    @FXML
    private TableColumn<Product, Float> manufacturerOfficialAddressTownXColumn;
    @FXML
    private TableColumn<Product, Integer> manufacturerOfficialAddressTownYColumn;
    @FXML
    private TableColumn<Product, Integer> manufacturerOfficialAddressTownZColumn;

    @FXML
    private Tab visualTab;
    @FXML
    private AnchorPane visualPane;

    @FXML
    public void initialize() {
        colorMap = new HashMap<>();
        infoMap = new HashMap<>();
        random = new Random();

        languageComboBox.setItems(FXCollections.observableArrayList(localeMap.keySet()));
        languageComboBox.setStyle("-fx-font: 13px \"Sergoe UI\";");
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            localizator.setBundle(ResourceBundle.getBundle("locales/gui", localeMap.get(newValue)));
            changeLanguage();
        });

        idColumn.setCellValueFactory(product -> new SimpleLongProperty(product.getValue().getId()).asObject());
        ownerColumn.setCellValueFactory(product -> new SimpleStringProperty(product.getValue().getCreator()));
        nameColumn.setCellValueFactory(product -> new SimpleStringProperty(product.getValue().getName()));
        coordinatesXColumn.setCellValueFactory(product -> new SimpleIntegerProperty(product.getValue().getCoordinates().getX()).asObject());
        coordinatesYColumn.setCellValueFactory(product -> new SimpleIntegerProperty(product.getValue().getCoordinates().getY()).asObject());
        creationDateColumn.setCellValueFactory(product -> new SimpleStringProperty(localizator.getDate(product.getValue().getCreationDate())));
        priceColumn.setCellValueFactory(product -> new SimpleIntegerProperty(product.getValue().getPrice()).asObject());
        partNumberColumn.setCellValueFactory(product -> new SimpleStringProperty(product.getValue().getPartNumber()));
        manufactureCostColumn.setCellValueFactory(product -> new SimpleIntegerProperty(product.getValue().getManufactureCost()).asObject());
        unitOfMeasureColumn.setCellValueFactory(product -> new SimpleStringProperty(product.getValue().getUnitOfMeasure().toString()));
        manufacturerIdColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null) {
                return new SimpleLongProperty(product.getValue().getManufacturer().getId()).asObject();
            }
            return null;
        });
        manufacturerNameColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null) {
                return new SimpleStringProperty(product.getValue().getManufacturer().getName());
            }
            return null;
        });
        manufacturerEmployeesCountColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null) {
                return new SimpleIntegerProperty(product.getValue().getManufacturer().getEmployeesCount()).asObject();
            }
            return null;
        });
        manufacturerTypeColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null) {
                return new SimpleStringProperty(product.getValue().getManufacturer().getType().toString());
            }
            return null;
        });
        manufacturerOfficialAddressStreetColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null && product.getValue().getManufacturer().getOfficialAddress() != null) {
                return new SimpleStringProperty(product.getValue().getManufacturer().getOfficialAddress().getStreet());
            }
            return null;
        });
        manufacturerOfficialAddressTownXColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null && product.getValue().getManufacturer().getOfficialAddress() != null) {
                return new SimpleFloatProperty(product.getValue().getManufacturer().getOfficialAddress().getTown().getX()).asObject();
            }
            return null;
        });
        manufacturerOfficialAddressTownYColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null && product.getValue().getManufacturer().getOfficialAddress() != null) {
                return new SimpleIntegerProperty(product.getValue().getManufacturer().getOfficialAddress().getTown().getY()).asObject();
            }
            return null;
        });
        manufacturerOfficialAddressTownZColumn.setCellValueFactory(product -> {
            if (product.getValue().getManufacturer() != null && product.getValue().getManufacturer().getOfficialAddress() != null) {
                return new SimpleIntegerProperty(product.getValue().getManufacturer().getOfficialAddress().getTown().getZ()).asObject();
            }
            return null;
        });

        tableTable.setRowFactory(tableView -> {
            var row = new TableRow<Product>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                    doubleClickUpdate(row.getItem());
                }
            });
            return row;
        });
        refresh();
        //visualTab.setOnSelectionChanged(event -> visualise(false)); //todo тут должна быть визуализация коллекции
    }

    @FXML
    public void exit() {
        System.exit(0);
    }

    @FXML
    public void logout() { //TODO нарисовать кнопочку
        SessionHandler.setCurrentUser(null);
        SessionHandler.setCurrentLanguage("Русский");
        setRefreshing(false);
        authCallback.run();
    }

    @FXML
    public void help() {
        try {
            ConnectionManager.getInstance().send(new Request("Help", SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive();
            String help = (String) response.getExecutionStatus().getAnswer().getAnswer(); //todo дай бог оно возвращает строку
            DialogManager.createAlert(localizator.getKeyString("Help"), localizator.getKeyString("HelpResult"), Alert.AlertType.INFORMATION, true);
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    private void info() {
        // TODO: Реализовать обработку кнопки "Info"
    }

    @FXML
    private void averageOfManufactureCost() {
        // TODO: Реализовать обработку кнопки "Average of Manufacture Cost"
    }

    @FXML
    private void minByName() {
        // TODO: Реализовать обработку кнопки "Min by Name"
    }

    @FXML
    private void clear() {
        // TODO: Реализовать обработку кнопки "Clear"
    }

    @FXML
    private void printFieldAscendingPartNumber() {
        // TODO: Реализовать обработку кнопки "Print Field Ascending Part Number"
    }

    @FXML
    private void removeGreaterKey() {
        // TODO: Реализовать обработку кнопки "Remove Greater Key"
    }

    @FXML
    private void add() {
        editController.clear();
        editController.show();
        Product product = editController.getProduct();
        if (product != null) {
            try {
                ConnectionManager.getInstance().send(new Request("add", product, SessionHandler.getCurrentUser()));
                Response response = ConnectionManager.getInstance().receive();
                DialogManager.createAlert(localizator.getKeyString("Add"), localizator.getKeyString("AddResult"), Alert.AlertType.INFORMATION, false);
            } catch (ClassNotFoundException | IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            }
        }
        loadCollection();
    }

    @FXML
    private void removeById() {
        // TODO: Реализовать обработку кнопки "Remove by ID"
    }

    @FXML
    private void removeGreater() {
        // TODO: Реализовать обработку кнопки "Remove Greater"
    }

    @FXML
    private void executeScript() {
        // TODO: Реализовать обработку кнопки "Execute Script"
    }


    private void doubleClickUpdate(Product product) {
        doubleClickUpdate(product, true); // умом
    }

    private void doubleClickUpdate(Product product, boolean ignoreAnotherUser) {
        //todo по двойному клику нам надо изменять отдельные поля объекта
    }

    public void changeLanguage() { //todo поменять на наши кнопки
        /*
        userLabel.setText(localizator.getKeyString("UserLabel") + " " + SessionHandler.getCurrentUser().getName());

        exitButton.setText(localizator.getKeyString("Exit"));
        logoutButton.setText(localizator.getKeyString("LogOut"));
        helpButton.setText(localizator.getKeyString("Help"));
        infoButton.setText(localizator.getKeyString("Info"));
        addButton.setText(localizator.getKeyString("Add"));
        updateButton.setText(localizator.getKeyString("Update"));
        removeByIdButton.setText(localizator.getKeyString("RemoveByID"));
        clearButton.setText(localizator.getKeyString("Clear"));
        executeScriptButton.setText(localizator.getKeyString("ExecuteScript"));
        headButton.setText(localizator.getKeyString("Head"));
        addIfMaxButton.setText(localizator.getKeyString("AddIfMax"));
        addIfMinButton.setText(localizator.getKeyString("AddIfMin"));
        sumOfPriceButton.setText(localizator.getKeyString("SumOfPrice"));
        filterByPriceButton.setText(localizator.getKeyString("FilterByPrice"));
        filterContainsPartNumberButton.setText(localizator.getKeyString("FilterContainsPartNumber"));

        tableTab.setText(localizator.getKeyString("TableTab"));
        visualTab.setText(localizator.getKeyString("VisualTab"));

        ownerColumn.setText(localizator.getKeyString("Owner"));
        nameColumn.setText(localizator.getKeyString("Name"));
        dateColumn.setText(localizator.getKeyString("CreationDate"));
        priceColumn.setText(localizator.getKeyString("Price"));
        partNumberColumn.setText(localizator.getKeyString("PartNumber"));
        unitOfMeasureColumn.setText(localizator.getKeyString("UnitOfMeasure"));

        manufacturerIdColumn.setText(localizator.getKeyString("ManufacturerId"));
        manufacturerNameColumn.setText(localizator.getKeyString("ManufacturerName"));
        manufacturerEmployeesCountColumn.setText(localizator.getKeyString("ManufacturerEmployeesCount"));
        manufacturerTypeColumn.setText(localizator.getKeyString("ManufacturerType"));
        manufacturerStreetColumn.setText(localizator.getKeyString("ManufacturerStreet"));
        manufacturerZipCodeColumn.setText(localizator.getKeyString("ManufacturerZipCode"));

        editController.changeLanguage();

        loadCollection();
        */
    }

    public void setCollection(List<Product> collection) {
        this.collection = collection;
        tableTable.setItems(FXCollections.observableArrayList(collection));
    }

    public void setAuthCallback(Runnable authCallback) {
        this.authCallback = authCallback;
    }

    public void refresh() {
        ExecutorService refresher = Executors.newSingleThreadExecutor();
        refresher.submit(() -> {
            while (isRefreshing()) {
                Platform.runLater(this::loadCollection);
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    private void loadCollection() {
        try {
            System.out.println("Refreshing collection...");
            ConnectionManager.getInstance().send(new Request("show", SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive();
            setCollection((List<Product>) response.getExecutionStatus().getAnswer().getAnswer()); //todo pizdec
            //visualise(true);
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    public void setEditController(EditController editController) {
        this.editController = editController;
        editController.changeLanguage();
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }
}
