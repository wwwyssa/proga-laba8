package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class EditController {
    private Stage stage;
    private Product product;
    private Localizator localizator;

    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label partNumberLabel;
    @FXML
    private Label manufactureCostLabel;
    @FXML
    private Label unitOfMeasureLabel;
    @FXML
    private Label mNameLabel;
    @FXML
    private Label mEmployeesCountLabel;
    @FXML
    private Label mTypeLabel;
    @FXML
    private Label mStreetLabel;

    @FXML
    private TextField nameField;
    @FXML
    private TextField productXField;
    @FXML
    private TextField productYField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField partNumberField;
    @FXML
    private TextField manufactureCostField;
    @FXML
    private TextField mNameField;
    @FXML
    private TextField mEmployeesCountField;
    @FXML
    private TextField mStreetField;
    @FXML
    private TextField locXField;
    @FXML
    private TextField locYField;
    @FXML
    private TextField locZField;
    @FXML
    private ChoiceBox<String> unitOfMeasureBox;
    @FXML
    private ChoiceBox<String> mTypeBox;

    @FXML
    private Button cancelButton;

    private void addListenerToTextField(TextField field, String regex) {
        field.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches(regex)) {
                field.setText(oldValue);
            } else if (newValue.matches(".+-.*")) {
                Platform.runLater(field::clear);
            }
        });
    }

    @FXML
    void initialize() {
        cancelButton.setOnAction(event -> stage.close());

        unitOfMeasureBox.setItems(FXCollections.observableArrayList(Arrays.stream(UnitOfMeasure.values()).map(Enum::toString).collect(Collectors.toList())));
        unitOfMeasureBox.setStyle("-fx-font: 12px \"Sergoe UI\";");

        mTypeBox.setItems(FXCollections.observableArrayList(Arrays.stream(OrganizationType.values()).map(Enum::toString).collect(Collectors.toList())));
        mTypeBox.setStyle("-fx-font: 12px \"Sergoe UI\";");

        addListenerToTextField(productXField, "[-\\d]{0,10}");
        addListenerToTextField(productYField, "[-\\d]{0,10}");
        addListenerToTextField(priceField, "\\d{0,10}");
        addListenerToTextField(manufactureCostField, "[-\\d]{0,10}");
        addListenerToTextField(mEmployeesCountField, "[-\\d]{0,10}");
        addListenerToTextField(locXField, "[-\\d]{0,10}\\.?\\d{0,10}");
        addListenerToTextField(locYField, "[-\\d]{0,10}");
        addListenerToTextField(locZField, "[-\\d]{0,10}");
    }

    private void checkTextFieldNotEmpty(TextField field, String localizatorKey, ArrayList<String> errors) {
        if (field.getText().isEmpty()) {
            errors.add("- " + localizator.getKeyString(localizatorKey) + " " + localizator.getKeyString("CannotBeEmpty"));
        }
    }
    private void checkBoxNotEmpty(ChoiceBox<String> box, String localizatorKey, ArrayList<String> errors) {
        if (box.getValue() == null || box.getValue().isEmpty()) {
            errors.add("- " + localizator.getKeyString(localizatorKey) + " " + localizator.getKeyString("CannotBeEmpty"));
        }
    }

    @FXML
    public void ok() { // ок
        nameField.setText(nameField.getText().trim());
        partNumberField.setText(partNumberField.getText().trim());
        manufactureCostField.setText(manufactureCostField.getText().trim());
        mNameField.setText(mNameField.getText().trim());
        mStreetField.setText(mStreetField.getText().trim());
        locXField.setText(locXField.getText().trim());

        ArrayList<String> errors = new ArrayList<>();
        checkTextFieldNotEmpty(nameField, "Name", errors);
        checkTextFieldNotEmpty(productXField, "CoordinatesX", errors);
        checkTextFieldNotEmpty(productYField, "CoordinatesY", errors);
        checkTextFieldNotEmpty(priceField, "Price", errors);
        checkTextFieldNotEmpty(partNumberField, "PartNumber", errors);
        checkTextFieldNotEmpty(manufactureCostField, "ManufactureCost", errors);
        checkBoxNotEmpty(unitOfMeasureBox, "UnitOfMeasure", errors);

        checkTextFieldNotEmpty(mNameField, "ManufacturerName", errors);
        checkTextFieldNotEmpty(mEmployeesCountField, "ManufacturerEmployeesCount", errors);
        checkBoxNotEmpty(mTypeBox, "ManufacturerType", errors);
        checkTextFieldNotEmpty(mStreetField, "ManufacturerStreet", errors);
        checkTextFieldNotEmpty(locXField, "ManufacturerTownX", errors);
        checkTextFieldNotEmpty(locYField, "ManufacturerTownY", errors);
        checkTextFieldNotEmpty(locZField, "ManufacturerTownZ", errors);

        if (!errors.isEmpty()) {
            DialogManager.createAlert(localizator.getKeyString("Error"), String.join("\n", errors), Alert.AlertType.ERROR, false);
        } else {
            Organization organization = new Organization(
                    1,
                    mNameField.getText(),
                    Integer.parseInt(mEmployeesCountField.getText()),
                    OrganizationType.valueOf(mTypeBox.getValue()),
                    new Address(
                            mStreetField.getText(),
                            new Location(
                                    Float.parseFloat(locXField.getText()),
                                    Integer.parseInt(locYField.getText()),
                                    Integer.parseInt(locZField.getText())
                            )
                    )
            );
            Product newProduct = new Product(
                    1,
                    nameField.getText(),
                    new Coordinates(Integer.parseInt(productXField.getText()), (int) Long.parseLong(productYField.getText())),
                    LocalDateTime.now(),
                    Integer.parseInt(priceField.getText()),
                    partNumberField.getText(),
                    Integer.parseInt(manufactureCostField.getText()),
                    UnitOfMeasure.valueOf(unitOfMeasureBox.getValue()),
                    organization,
                    SessionHandler.getCurrentUser().getName()
            );
            if (!newProduct.isValid()) {
                DialogManager.alert("InvalidProduct", localizator);
            } else {
                product = newProduct;
                stage.close();
            }
        }
    }

    public Product getProduct() {
        var tmpProduct = product;
        product = null;
        return tmpProduct;
    }

    public void clear() {
        nameField.clear();
        productXField.clear();
        productYField.clear();
        priceField.clear();
        partNumberField.clear();
        manufactureCostField.clear();
        unitOfMeasureBox.valueProperty().setValue(null);

        mNameField.clear();
        mEmployeesCountField.clear();
        mTypeBox.valueProperty().setValue(null);
        mStreetField.clear();
    }

    public void fill(Product product) {
        nameField.setText(product.getName());
        productXField.setText(Integer.toString(product.getCoordinates().getX()));
        productYField.setText(Long.toString(product.getCoordinates().getY()));
        priceField.setText(Long.toString(product.getPrice()));
        partNumberField.setText(product.getPartNumber());
        manufactureCostField.setText(Integer.toString(product.getManufactureCost()));
        unitOfMeasureBox.setValue(product.getUnitOfMeasure() == null ? null : product.getUnitOfMeasure().toString());
        if (product.getManufacturer() != null) {
            var manufacturer = product.getManufacturer();
            mNameField.setText(manufacturer.getName());
            mEmployeesCountField.setText(Long.toString(manufacturer.getEmployeesCount()));
            mTypeBox.setValue(manufacturer.getType().toString());
            mStreetField.setText(manufacturer.getOfficialAddress().getStreet());
            locXField.setText(Float.toString(manufacturer.getOfficialAddress().getTown().getX()));
            locYField.setText(Integer.toString(manufacturer.getOfficialAddress().getTown().getY()));
            locZField.setText(manufacturer.getOfficialAddress().getTown().getZ() == null ? "" : Integer.toString(manufacturer.getOfficialAddress().getTown().getZ()));

        } else {
            mNameField.clear();
            mEmployeesCountField.clear();
            mTypeBox.valueProperty().setValue(null);
            mStreetField.clear();

        }
    }

    public void changeLanguage() {
        titleLabel.setText(localizator.getKeyString("EditTitle"));
        nameLabel.setText(localizator.getKeyString("Name"));
        priceLabel.setText(localizator.getKeyString("Price"));
        partNumberLabel.setText(localizator.getKeyString("PartNumber"));
        unitOfMeasureLabel.setText(localizator.getKeyString("UnitOfMeasure"));
        manufactureCostLabel.setText(localizator.getKeyString("ManufactureCost"));
        mNameLabel.setText(localizator.getKeyString("ManufacturerName"));
        mEmployeesCountLabel.setText(localizator.getKeyString("ManufacturerEmployeesCount"));
        mTypeLabel.setText(localizator.getKeyString("ManufacturerType"));
        mStreetLabel.setText(localizator.getKeyString("ManufacturerStreet"));
        cancelButton.setText(localizator.getKeyString("CancelButton"));
    }

    public void show() {
        if (!stage.isShowing()) stage.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }
}