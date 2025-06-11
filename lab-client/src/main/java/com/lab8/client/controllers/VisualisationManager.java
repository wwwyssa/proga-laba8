package com.lab8.client.controllers;

import com.lab8.client.managers.DialogManager;
import com.lab8.common.models.Product;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Random;
import java.util.List;

import static java.lang.Math.max;

public class VisualisationManager {

    Random random = new Random();
    private final AnchorPane visualPane;

    public VisualisationManager(AnchorPane visualPane) {
        this.visualPane = visualPane;
    }

    public void drawCollection(List<Product> collection) {
        visualPane.getChildren().clear(); // Clear previous drawings
        for (Product product : collection) {
            double centerX = product.getCoordinates().getX();
            double centerY = product.getCoordinates().getY();
            double radius = Math.sqrt(product.getPrice());
            String color = String.format("rgb(%d, %d, %d)", random.nextInt(256), random.nextInt(256), random.nextInt(256));
            drawProduct(centerX, centerY, product); // Pass the Product object
        }
    }

    public void drawCircle(double centerX, double centerY, double radius, String color) {
        Circle circle = new Circle();
        circle.setCenterX(centerX);
        circle.setCenterY(centerY);
        circle.setRadius(radius);
        circle.setStyle("-fx-fill: " + color + ";");

        visualPane.getChildren().add(circle);
    }


    public void drawProduct(double centerX, double centerY, Product product) {

        int randomNumber = random.nextInt(8) + 1;
        Image image = new Image(getClass().getResource("/images/" + randomNumber + ".png").toExternalForm());

        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(25); // Set width
        imageView.setFitHeight(25); // Set height
        imageView.setPreserveRatio(true);
        imageView.setX(centerX);
        imageView.setY(centerY);



        // Add click event to display Product ID
        imageView.setOnMouseClicked(event -> {
            System.out.println("Product ID: " + product.getId());
            DialogManager.createAlert("Product ID", "ID: " + product.getId(), Alert.AlertType.INFORMATION, false);
            double newSize = Math.sqrt(product.getPrice());
            imageView.setFitWidth(max(newSize, 25));
            imageView.setFitHeight(max(newSize, 25));
            drawFlag(centerX, centerY, newSize);
        });

        visualPane.getChildren().add(imageView);
    }

    public void drawFlag(double centerX, double centerY, double size) {
        Image image = new Image(getClass().getResource("/images/boom.gif").toExternalForm());

        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(size); // Set width
        imageView.setFitHeight(size); // Set height
        imageView.setPreserveRatio(true);
        imageView.setX(centerX); // Center the flag horizontally
        imageView.setY(centerY); // Center the flag vertically

        // Add the ImageView to the pane
        visualPane.getChildren().add(imageView);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            visualPane.getChildren().remove(imageView);
        }));
        timeline.setCycleCount(1); // Run only once
        timeline.play();
    }
}