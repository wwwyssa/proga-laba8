package com.lab8.client.controllers;

import com.lab8.common.models.Product;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import java.util.Random;
import java.util.List;
import java.util.Random;

public class VisualisationManager {

    Random random = new Random();
    private final AnchorPane visualPane;

    public VisualisationManager(AnchorPane visualPane) {
        this.visualPane = visualPane;
    }

    public void drawCollection(List<Product> collection){
        for (Product product : collection) {
            double centerX = product.getCoordinates().getX();
            double centerY = product.getCoordinates().getY();
            double radius = Math.sqrt(product.getPrice());
            String color = String.format("rgb(%d, %d, %d)", random.nextInt(256), random.nextInt(256), random.nextInt(256));
            drawCircle(centerX, centerY, radius, color);
            drawFlag(centerX, centerY); // Adjust position to center the image
            drawHohol(centerX, centerY); // Adjust position to center the image
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


    public void drawHohol(double centerX, double centerY) {
        Image image = new Image(getClass().getResource("/images/pig.png").toExternalForm());

        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(50); // Set width
        imageView.setFitHeight(50); // Set height
        imageView.setPreserveRatio(true);
        imageView.setX(centerX);
        imageView.setY(centerY);
        // Add the ImageView to the pane
        visualPane.getChildren().add(imageView);
    }

    public void drawFlag(double centerX, double centerY) {
        Image image = new Image(getClass().getResource("/images/flag.png").toExternalForm());

        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(50); // Set width
        imageView.setFitHeight(50); // Set height
        imageView.setPreserveRatio(true);
        imageView.setX(centerX); // Center the flag horizontally
        imageView.setY(centerY); // Center the flag vertically
        // Add the ImageView to the pane
        visualPane.getChildren().add(imageView);
    }
}