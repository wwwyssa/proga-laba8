package com.lab8.client.controllers;

import com.lab8.client.util.Localizator;
import com.lab8.common.models.Product;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class ItemViewController {

    private Localizator localizator;

    @FXML
    public Label titleLabel;
    @FXML
    public Button closeButton;
    @FXML
    public TableView<Product> tableTable;
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

    private Stage stage;

    @FXML
    public void initialize() {
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
        // TableViewManager.setProducts(tableTable); todo в отдельный класс
    }
    // todo добавить перевод на руский
    public void setCollection(List<Product> collection) {
        tableTable.setItems(FXCollections.observableArrayList(collection));
    }
    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }
}