package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.ConnectionManager;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.managers.ExecuteScript;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.Organization;
import com.lab8.common.models.Product;
import com.lab8.common.util.Request;
import com.lab8.common.util.Response;
import com.lab8.common.util.executions.ExecutionResponse;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController {
    private Localizator localizator;
    private final UpdatingManager updatingManager = new UpdatingManager(this);
    private Runnable authCallback;

    private final HashMap<String, Locale> localeMap = new HashMap<>() {{
        put("Русский", new Locale("ru", "RU"));
        put("English(CA)", new Locale("en", "CA"));
        put("Latvian", new Locale("lv"));
        put("Slovenian", new Locale("sl"));
    }};

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
        VisualisationManager visualisationManager = new VisualisationManager(visualPane);
        languageComboBox.setItems(FXCollections.observableArrayList(localeMap.keySet()));
        languageComboBox.setStyle("-fx-font: 13px \"Sergoe UI\";");
        languageComboBox.setValue(SessionHandler.getCurrentLanguage());
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            localizator.setBundle(ResourceBundle.getBundle("locales/gui", localeMap.get(newValue)));
            changeLanguage();
        });

        //тут непосредственно сортировка todo запихать в отдельный класс???
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

        tableTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        updatingManager.refresh();
        visualTab.setOnSelectionChanged(event -> visualisationManager.drawCollection(tableTable.getItems(), localizator));

    }

    @FXML
    public void exit() {
        System.exit(0);
    }

    @FXML
    public void logout() {
        SessionHandler.setCurrentUser(null);
        SessionHandler.setCurrentLanguage("Русский");
        updatingManager.stopRefreshing();
        authCallback.run();
    }

    @FXML
    public void help() {
        try {
            ConnectionManager.getInstance().send(new Request("help", SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive();
            DialogManager.createAlert(localizator.getKeyString("Help"), localizator.getKeyString("HelpResult"), Alert.AlertType.INFORMATION, true);
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    private void info() {
        try {
            ConnectionManager.getInstance().send(new Request("info", SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive();
            List<String> infoList = (List<String>) response.getExecutionStatus().getAnswer().getAnswer();
            DialogManager.createAlert(localizator.getKeyString("Info"), MessageFormat.format(localizator.getKeyString("InfoResult"), infoList.get(0), infoList.get(1), infoList.get(2), infoList.get(3)), Alert.AlertType.INFORMATION, true);
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    private void averageOfManufactureCost() {
        try {
            ConnectionManager.getInstance().send(new Request("averageOfManufactureCost", SessionHandler.getCurrentUser()));
            ExecutionResponse<?> executionResponse = ConnectionManager.getInstance().receive().getExecutionStatus();
            String averageCost = executionResponse.getAnswer().getAnswer().toString().split(": ")[1];
            DialogManager.createAlert(localizator.getKeyString("AverageOfManufactureCost"), localizator.getKeyString("AverageOfManufactureCostResult") + averageCost, Alert.AlertType.INFORMATION, true);
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    private void clear() {
        try {
            ConnectionManager.getInstance().send(new Request("clear", SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive();
            if (response.getExecutionStatus().getExitCode()) {
                DialogManager.createAlert(localizator.getKeyString("Clear"), localizator.getKeyString("ClearResult"), Alert.AlertType.INFORMATION, false);
                updatingManager.loadCollection();
            } else {
                DialogManager.createAlert(localizator.getKeyString("Error"), response.getExecutionStatus().getAnswer().toString(), Alert.AlertType.ERROR, false);
            }
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    public void showItemWindow(List<Product> products) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/itemView.fxml"));
            Parent root = loader.load();
            ItemViewController controller = loader.getController();
            controller.setLocalizator(localizator);
            controller.setCollection(products);
            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setTitle("Информация о продукте");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            DialogManager.alert("Error", localizator);
        }
    }

    @FXML
    private void minByName() {
        try {
            ConnectionManager.getInstance().send(new Request("minByName", SessionHandler.getCurrentUser()));
            ExecutionResponse<?> ExecutionResponse = ConnectionManager.getInstance().receive().getExecutionStatus();
            if (ExecutionResponse.getExitCode()) {
                showItemWindow((List<Product>) ExecutionResponse.getAnswer().getAnswer());
            } else {
                DialogManager.createAlert(localizator.getKeyString("Error"), ExecutionResponse.getAnswer().toString(), Alert.AlertType.ERROR, false);
            }
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    private void printFieldAscendingPartNumber() {
        try {
            ConnectionManager.getInstance().send(new Request("printFieldAscendingPartNumber", SessionHandler.getCurrentUser()));
            ExecutionResponse<?> ExecutionResponse = ConnectionManager.getInstance().receive().getExecutionStatus();
            if (ExecutionResponse.getExitCode()) {
                showItemWindow((List<Product>) ExecutionResponse.getAnswer().getAnswer());
            } else {
                DialogManager.createAlert(localizator.getKeyString("Error"), ExecutionResponse.getAnswer().toString(), Alert.AlertType.ERROR, false);
            }
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    private void idCommand(String commandLocalizatorKey, String commandInRequest) {
        Optional<String> input = DialogManager.createDialog(localizator.getKeyString(commandLocalizatorKey), "ID: ");
        if (input.isPresent()) {
            try {
                long id = Long.parseLong(input.get());
                ConnectionManager.getInstance().send(new Request(commandInRequest + ' ' + id, SessionHandler.getCurrentUser()));
                Response response = ConnectionManager.getInstance().receive();
                if (response.getExecutionStatus().getExitCode()) {
                    DialogManager.createAlert(localizator.getKeyString(commandLocalizatorKey), localizator.getKeyString(commandLocalizatorKey + "Suc"), Alert.AlertType.INFORMATION, false);
                } else {
                    DialogManager.createAlert(localizator.getKeyString("Error"), response.getExecutionStatus().getAnswer().toString(), Alert.AlertType.ERROR, false);
                }
            } catch (NumberFormatException e) {
                DialogManager.createAlert(localizator.getKeyString("Error"), localizator.getKeyString("InvalidIDFormat"), Alert.AlertType.ERROR, false);
            } catch (ClassNotFoundException | IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            }
        }
        updatingManager.loadCollection();
    }

    @FXML
    private void removeGreaterKey() {
        idCommand("RemoveGreaterKey", "removeGreaterKey");
    }

    @FXML
    private void removeById() {
        idCommand("RemoveByID", "removeById");
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
                if (response.getExecutionStatus().getExitCode()){
                    DialogManager.createAlert(localizator.getKeyString("Add"), localizator.getKeyString("AddResult"), Alert.AlertType.INFORMATION, false);
                }
                else {
                    DialogManager.createAlert(localizator.getKeyString("Error"), response.getExecutionStatus().getAnswer().toString(), Alert.AlertType.ERROR, false);
                }
                updatingManager.loadCollection();
            } catch (ClassNotFoundException | IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            }
        }
    }

    @FXML
    private void removeGreater() {
        try {
            editController.clear();
            editController.show();
            Product product = editController.getProduct();
            ConnectionManager.getInstance().send(new Request("removeGreater", product, SessionHandler.getCurrentUser()));
            Response response = ConnectionManager.getInstance().receive(); // fixme и че это
        } catch (ClassNotFoundException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
        updatingManager.loadCollection();
    }

    @FXML
    private void executeScript() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Script File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            ExecuteScript.runScript(selectedFile.getAbsolutePath(), SessionHandler.getCurrentUser());

        } else {
            DialogManager.createAlert(localizator.getKeyString("Error"), localizator.getKeyString("FileNotSelected"), Alert.AlertType.ERROR, false);
        }
    }


    private void doubleClickUpdate(Product product) {
        // System.out.print("Double click on row: " + product);
        if (product.getCreator().equals(SessionHandler.getCurrentUser().getName())) {
            editController.fill(product);
            editController.show();
            Product updatedProduct = editController.getProduct();
            if (updatedProduct != null) {
                try {
                    updatedProduct.setId(product.getId());
                    Organization manufacturer = updatedProduct.getManufacturer();
                    manufacturer.setId(product.getManufacturer().getId());
                    updatedProduct.setManufacturer(manufacturer);
                    ConnectionManager.getInstance().send(new Request("update", updatedProduct, SessionHandler.getCurrentUser()));
                    Response response = ConnectionManager.getInstance().receive();
                    if (response.getExecutionStatus().getExitCode()) {
                        DialogManager.createAlert(localizator.getKeyString("update"), localizator.getKeyString("update"), Alert.AlertType.INFORMATION, false);
                        // Обновление коллекции в TableView
                        int index = tableTable.getItems().indexOf(product);
                        if (index != -1) {
                            tableTable.getItems().set(index, updatedProduct);
                        }
                    } else {
                        DialogManager.createAlert(localizator.getKeyString("Error"), response.getExecutionStatus().getAnswer().toString(), Alert.AlertType.ERROR, false);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    DialogManager.alert("UnavailableError", localizator);
                }
            }
        } else {
            DialogManager.createAlert(localizator.getKeyString("Error"), localizator.getKeyString("NotYourProduct"), Alert.AlertType.ERROR, false);
        }
    }

    public void changeLanguage() {
        userLabel.setText(localizator.getKeyString("UserLabel") + " " + SessionHandler.getCurrentUser().getName());

        exitButton.setText(localizator.getKeyString("Exit"));
        logoutButton.setText(localizator.getKeyString("LogOut"));
        helpButton.setText(localizator.getKeyString("Help"));
        infoButton.setText(localizator.getKeyString("Info"));
        addButton.setText(localizator.getKeyString("Add"));
        removeByIdButton.setText(localizator.getKeyString("RemoveByID"));
        clearButton.setText(localizator.getKeyString("Clear"));
        executeScriptButton.setText(localizator.getKeyString("ExecuteScript"));
        removeGreaterButton.setText(localizator.getKeyString("RemoveGreater"));
        removeGreaterKeyButton.setText(localizator.getKeyString("RemoveGreaterKey"));
        printFieldAscendingPartNumberButton.setText(localizator.getKeyString("PrintFieldAscendingPartNumber"));
        averageOfManufactureCostButton.setText(localizator.getKeyString("AverageOfManufactureCost"));
        minByNameButton.setText(localizator.getKeyString("MinByName"));

        tableTab.setText(localizator.getKeyString("TableTab"));
        visualTab.setText(localizator.getKeyString("VisualTab"));

        idColumn.setText(localizator.getKeyString("ID"));
        ownerColumn.setText(localizator.getKeyString("Owner"));
        nameColumn.setText(localizator.getKeyString("Name"));
        coordinatesXColumn.setText(localizator.getKeyString("CoordinatesX"));
        coordinatesYColumn.setText(localizator.getKeyString("CoordinatesY"));
        creationDateColumn.setText(localizator.getKeyString("CreationDate"));
        priceColumn.setText(localizator.getKeyString("Price"));
        partNumberColumn.setText(localizator.getKeyString("PartNumber"));
        manufactureCostColumn.setText(localizator.getKeyString("ManufactureCost"));
        unitOfMeasureColumn.setText(localizator.getKeyString("UnitOfMeasure"));
        manufacturerIdColumn.setText(localizator.getKeyString("ManufacturerId"));
        manufacturerNameColumn.setText(localizator.getKeyString("ManufacturerName"));
        manufacturerEmployeesCountColumn.setText(localizator.getKeyString("ManufacturerEmployeesCount"));
        manufacturerTypeColumn.setText(localizator.getKeyString("ManufacturerType"));
        manufacturerOfficialAddressStreetColumn.setText(localizator.getKeyString("ManufacturerStreet"));
        manufacturerOfficialAddressTownXColumn.setText(localizator.getKeyString("ManufacturerTownX"));
        manufacturerOfficialAddressTownYColumn.setText(localizator.getKeyString("ManufacturerTownY"));
        manufacturerOfficialAddressTownZColumn.setText(localizator.getKeyString("ManufacturerTownZ"));


        editController.changeLanguage();
        //updatingManager.loadCollection();
    }

    public void setCollection(List<Product> collection) {
        //this.collection = collection;
        tableTable.setItems(FXCollections.observableArrayList(collection));
    }

    public void setAuthCallback(Runnable authCallback) {
        this.authCallback = authCallback;
    }

    public void setEditController(EditController editController) {
        this.editController = editController;
        editController.changeLanguage();
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
        updatingManager.setLocalizator(localizator);
    }
}
