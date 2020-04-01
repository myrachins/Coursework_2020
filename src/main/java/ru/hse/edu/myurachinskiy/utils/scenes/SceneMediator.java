package ru.hse.edu.myurachinskiy.utils.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.hse.edu.myurachinskiy.controllers.LinguisticValuesSelectController;
import ru.hse.edu.myurachinskiy.controllers.SeriesSelectController;
import ru.hse.edu.myurachinskiy.utils.AppSettings;
import ru.hse.edu.myurachinskiy.utils.alerts.AlertFactory;

import java.io.IOException;
import java.rmi.UnexpectedException;

public class SceneMediator {
    private SceneMediator() { }

    public static void showFirstScene(Stage primaryStage) throws IOException {
        Parent parent = loadScene("/views/series_select.fxml");
        setScene(primaryStage, parent);

        primaryStage.setWidth(AppSettings.START_WINDOW_WIDTH);
        primaryStage.setHeight(AppSettings.START_WINDOW_HEIGHT);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();
    }

    public static void changeScene(Initializable currentController, ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Parent parent = getNextScene(currentController);
            setScene(stage, parent);
        } catch (IOException e) {
            Alert alert = AlertFactory.getErrorAlert("App error", "Scene wasn't found", e.getMessage());
            alert.show();
        }
    }

    private static void setScene(Stage stage, Parent parent) {
        BorderPane root = new BorderPane();
        root.setCenter(parent);

        Group group = new Group(root);
        Scene scene = new Scene(group);

        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());

        stage.setScene(scene);
    }

    private static Parent getNextScene(Initializable currentController) throws IOException {
        if (currentController instanceof SeriesSelectController) {
            return loadScene("/views/linguistic_values_select.fxml");
        } else if (currentController instanceof LinguisticValuesSelectController) {
            return loadScene("/views/fuzzy_series_view.fxml");
        }
        throw new UnexpectedException("Controller \"" + currentController.toString() + "\" is not supported");
    }

    private static Parent loadScene(String resourceName) throws IOException {
        return FXMLLoader.load(SceneMediator.class.getResource(resourceName));
    }
}
