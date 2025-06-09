package com.lab8.client.controllers;

import com.lab8.client.Auth.SessionHandler;
import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.time.LocalDate;
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
    private Label hasManufacturer;
    @FXML
    private Label mNameLabel;
    @FXML
    private Label mEmployeesCountLabel;
    @FXML
    private Label mTypeLabel;
    @FXML
    private Label mStreetLabel;
    @FXML
    private Label mHasLocation;

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
    private ChoiceBox<String> hasManufacturerBox;
    @FXML
    private ChoiceBox<String> mTypeBox;
    @FXML
    private ChoiceBox<String> hasLocationBox;

    @FXML
    private Button cancelButton;

    @FXML
    void initialize() {
        cancelButton.setOnAction(event -> stage.close());
        var orgTypes = FXCollections.observableArrayList(
                Arrays.stream(OrganizationType.values()).map(Enum::toString).collect(Collectors.toList())
        );
        mTypeBox.setItems(orgTypes);
        mTypeBox.setStyle("-fx-font: 12px \"Sergoe UI\";");

        var unitOfMeasures = FXCollections.observableArrayList(
                Arrays.stream(UnitOfMeasure.values()).map(Enum::toString).collect(Collectors.toList())
        );
        unitOfMeasureBox.setItems(unitOfMeasures);
        unitOfMeasureBox.setStyle("-fx-font: 12px \"Sergoe UI\";");

        var hasManufacturer = FXCollections.observableArrayList("TRUE", "FALSE");
        hasManufacturerBox.setItems(hasManufacturer);
        hasManufacturerBox.setValue("FALSE");
        hasManufacturerBox.setStyle("-fx-font: 12px \"Sergoe UI\";");


        Arrays.asList(mNameField, mEmployeesCountField, mStreetField, mHasLocation, mTypeBox).forEach(field -> {
            field.disableProperty().bind(
                    hasManufacturerBox.getSelectionModel().selectedItemProperty().isEqualTo("FALSE")
            );
        });

        productXField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d]{0,11}")) {
                productXField.setText(oldValue);
            } else {
                if (newValue.matches(".+-.*")) {
                    Platform.runLater(() -> productXField.clear());
                } else if (
                        newValue.length() == 10 && Long.parseLong(newValue) > Integer.MAX_VALUE
                                || newValue.length() == 11 && Long.parseLong(newValue) < Integer.MIN_VALUE
                ) {
                    productXField.setText(oldValue);
                }
            }
        });

        productYField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d]{0,20}")) {
                productYField.setText(oldValue);
            } else {
                if (newValue.matches(".+-.*")) {
                    Platform.runLater(() -> productYField.clear());
                } else if (!newValue.isEmpty() && (
                        newValue.length() == 19 && new BigInteger(newValue).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                                || newValue.length() == 20 && new BigInteger(newValue).compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0
                )) {
                    productYField.setText(oldValue);
                }
            }
        });

        Arrays.asList(priceField, mEmployeesCountField).forEach(field -> {
            field.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!field.isDisabled()) {
                    if (!newValue.matches("\\d{0,19}")) {
                        field.setText(oldValue);
                    } else {
                        if (!newValue.isEmpty() && (
                                new BigInteger(newValue).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                                        || new BigInteger(newValue).compareTo(new BigInteger(String.valueOf(0))) <= 0
                        )) {
                            field.setText(oldValue);
                        }
                    }

                }
            });
        });
    }

    @FXML
    public void ok() {
        nameField.setText(nameField.getText().trim());
        partNumberField.setText(partNumberField.getText().trim());
        mNameField.setText(mNameField.getText().trim());
        mStreetField.setText(mStreetField.getText().trim());
        mHasLocation.setText(mHasLocation.getText().trim());

        var errors = new ArrayList<String>();

        Organization organization = null;
        if (hasManufacturerBox.getValue().equals("TRUE")) {
            if (mNameField.getText().isEmpty()) errors.add(
                    "- " + localizator.getKeyString("ManufacturerName") + " " + localizator.getKeyString("CannotBeEmpty")
            );
            if (mStreetField.getText().isEmpty()) errors.add(
                    "- " + localizator.getKeyString("ManufacturerStreet") + " " + localizator.getKeyString("CannotBeEmpty")
            );

            String zipCode = mHasLocation.getText();
            if (mHasLocation.getText().isEmpty()) {
                zipCode = null;
            } else if (zipCode.length() < 6) {
                errors.add("- " + localizator.getKeyString("ZipCodeLength"));
            }

            OrganizationType organizationType = null;
            if (mTypeBox.getValue() != null) {
                organizationType = OrganizationType.valueOf(mTypeBox.getValue());
            } else {
                errors.add("- " + localizator.getKeyString("ManufacturerType") + " " + localizator.getKeyString("CannotBeEmpty"));
            }

            organization = new Organization(
                    -1,
                    mNameField.getText(),
                    Integer.parseInt(mEmployeesCountField.getText()),
                    organizationType,
                    new Address(
                            mStreetField.getText(),
                            new Location(
                                    Float.parseFloat(locXField.getText()),
                                    Integer.parseInt(locYField.getText()),
                                    locZField.getText().isEmpty() ? null : Integer.parseInt(locZField.getText())
                            )
                    )
            );
        }

        if (nameField.getText().isEmpty()) errors.add(
                "- " + localizator.getKeyString("Name") + " " + localizator.getKeyString("CannotBeEmpty")
        );

        String partNumber = partNumberField.getText();
        if (partNumberField.getText().isEmpty()) partNumber = null;

        UnitOfMeasure unitOfMeasure = null;
        if (unitOfMeasureBox.getValue() != null) unitOfMeasure = UnitOfMeasure.valueOf(unitOfMeasureBox.getValue());

        int manufactureCost = 0;
        manufactureCost = Integer.parseInt(manufactureCostLabel.getText().trim());

        if (!errors.isEmpty()) {
            DialogManager.createAlert(localizator.getKeyString("Error"), String.join("\n", errors), Alert.AlertType.ERROR, false);
        } else {
            var newProduct = new Product(
                    -1,
                    nameField.getText(),
                    new Coordinates(Integer.parseInt(productXField.getText()), (int) Long.parseLong(productYField.getText())),
                    LocalDateTime.now(),
                    Integer.parseInt(priceField.getText()),
                    partNumber,
                    manufactureCost,
                    unitOfMeasure,
                    organization,
                    SessionHandler.getCurrentUser().getId()
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
        unitOfMeasureBox.valueProperty().setValue(null);
        hasManufacturerBox.valueProperty().setValue("FALSE");

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
        unitOfMeasureBox.setValue(product.getUnitOfMeasure() == null ? null : product.getUnitOfMeasure().toString());
        hasManufacturerBox.setValue(product.getManufacturer() == null ? "FALSE" : "TRUE");

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
        hasManufacturer.setText(localizator.getKeyString("HasManufacturer"));
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