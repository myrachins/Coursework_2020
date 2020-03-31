package ru.hse.edu.myurachinskiy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent canvas = FXMLLoader.load(getClass().getResource("/views/series_select.fxml"));

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        Group group = new Group(root);
        Scene scene = new Scene(group);

        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());

        primaryStage.setScene(scene);
        primaryStage.setHeight(1000);
        primaryStage.setWidth(1000);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();
    }
}