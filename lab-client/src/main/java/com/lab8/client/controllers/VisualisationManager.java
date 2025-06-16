package com.lab8.client.controllers;

import com.lab8.client.managers.DialogManager;
import com.lab8.client.util.Localizator;
import com.lab8.common.models.Product;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

import static java.lang.Math.max;

public class VisualisationManager {

    Random random = new Random();
    private final AnchorPane visualPane;

    public VisualisationManager(AnchorPane visualPane) {
        this.visualPane = visualPane;
    }

    public void drawCollection(List<Product> collection, Localizator localizator) {
        visualPane.getChildren().clear(); // Clear previous drawings
        for (Product product : collection) {
            double centerX = product.getCoordinates().getX();
            double centerY = product.getCoordinates().getY();
            double radius = Math.sqrt(product.getPrice());
            String color = String.format("rgb(%d, %d, %d)", random.nextInt(256), random.nextInt(256), random.nextInt(256));
            drawProduct(centerX, centerY, product, localizator); // Pass the Product object
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


    public void drawProduct(double centerX, double centerY, Product product, Localizator localizator) {

        int randomNumber = random.nextInt(8) + 1;
        Image image = new Image(getClass().getResource("/images/" + randomNumber + ".png").toExternalForm());

        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(25); // Set width
        imageView.setFitHeight(25); // Set height
        imageView.setPreserveRatio(true);
        imageView.setX(centerX);
        imageView.setY(centerY);




        imageView.setOnMouseClicked(event -> {
            System.out.println("Product ID: " + product.getId());
            List<Product> tmp = new ArrayList<>();
            tmp.add(product);
            showItemWindow(tmp, localizator);
            double newSize = Math.sqrt(product.getPrice());

            imageView.setFitWidth(max(newSize, 25));
            imageView.setFitHeight(max(newSize, 25));
            drawFlag(centerX, centerY, newSize);
        });

        visualPane.getChildren().add(imageView);
    }

    public void showItemWindow(List<Product> products, Localizator localizator) {
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