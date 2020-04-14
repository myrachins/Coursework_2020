package ru.hse.edu.myurachinskiy.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.edu.myurachinskiy.models.DataContext;
import ru.hse.edu.myurachinskiy.models.FuzzyAffiliation;
import ru.hse.edu.myurachinskiy.models.FuzzyPoint;
import ru.hse.edu.myurachinskiy.models.FuzzyPointsSeries;
import ru.hse.edu.myurachinskiy.utils.AppSettings;
import ru.hse.edu.myurachinskiy.utils.alerts.AlertFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class FuzzySeriesController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Double> series = DataContext.originalSeries.getSeries();
        DataContext.fuzzyPointsSeries = new FuzzyPointsSeries();

        for (int i = 0; i < series.size() - 1; i++) {
            double point = series.get(i + 1) - series.get(i);
            List<FuzzyAffiliation> affiliations = DataContext.linguisticFuzzySeries.getAffiliations(point);
            FuzzyPoint currentFuzzyPoint = new FuzzyPoint(affiliations);
            DataContext.fuzzyPointsSeries.addPoint(currentFuzzyPoint);
            seriesListView.getItems().add((i + 1) + ") " + currentFuzzyPoint.toString());
        }

        fileChooser = new FileChooser();
        fileChooser.setTitle("Save time series as ...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt", "*.txt"));

        currentForecastHorizon = Integer.parseInt(forecastHorizonTextField.getText());
        forecastHorizonTextField.textProperty().addListener((observable, oldValue, newValue)
                -> paramsChanged(currentBeginRange, currentEndRange, newValue));

        seriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        seriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Integer> selected = seriesListView.getSelectionModel().getSelectedIndices();
            if (selected == null || selected.isEmpty()) { return; }

            int minSelected = Collections.min(selected);
            int maxSelected = Collections.max(selected);
            seriesListView.getSelectionModel().selectRange(minSelected, maxSelected);
            paramsChanged(minSelected + 1, maxSelected + 1,
                    Integer.toString(currentForecastHorizon));
        });
    }

    public void onExport(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(new Stage());
        if (file == null) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()))) {
            for (String row : seriesListView.getItems()) {
                writer.write(row);
                writer.newLine();
            }
        } catch (IOException e) {
            Alert alert = AlertFactory.getErrorAlert("File error",
                    "Error while opening the file", e.getMessage());
            alert.show();
        }
    }

    public void onPredict(ActionEvent actionEvent) {
        List<FuzzyPoint> predictedPoints = DataContext.fuzzyPointsSeries
                .predict(currentBeginRange - 1, currentEndRange, currentForecastHorizon);
        predictedListView.getItems().clear();
        for (int i = 0; i < predictedPoints.size(); i++) {
            FuzzyPoint predictedPoint = predictedPoints.get(i);
            predictedListView.getItems().add((currentEndRange + 1 + i) + ") " + predictedPoint.toString());
        }
        predictedListView.setPrefHeight(predictedPoints.size() * AppSettings.ROW_HEIGHT_LIST_VIEW);
    }

    public void onIndex(ActionEvent actionEvent) {
        int indexStart = DataContext.fuzzyPointsSeries.index(currentBeginRange - 1, currentEndRange);
        seriesListView.getSelectionModel().clearSelection();
        seriesListView.getSelectionModel().selectRange(indexStart, indexStart + (currentEndRange - currentBeginRange) + 1);
        seriesListView.scrollTo(indexStart);
    }

    private void paramsChanged(int newBeginRange, int newEndRange, String newForecastHorizon) {
        try {
            currentBeginRange = newBeginRange;
            currentEndRange = newEndRange;
            currentForecastHorizon = Integer.parseInt(newForecastHorizon);
            int size = currentEndRange - currentBeginRange + 1;
            if (currentBeginRange <= 1 || currentEndRange <= 1
                    || currentBeginRange > DataContext.fuzzyPointsSeries.getSize()
                    || currentEndRange > DataContext.fuzzyPointsSeries.getSize()
                    || currentForecastHorizon <= 0 || size <= 0
                    || size + currentForecastHorizon > currentEndRange) {
                throw new IllegalArgumentException();
            }
            predictButton.setDisable(false);
            indexButton.setDisable(false);
        } catch (IllegalArgumentException e) {
            predictButton.setDisable(true);
            indexButton.setDisable(true);
        }
    }

    @FXML
    private ListView<String> seriesListView;
    @FXML
    private TextField forecastHorizonTextField;
    @FXML
    private Button predictButton;
    @FXML
    private ListView<String> predictedListView;
    @FXML
    private Button indexButton;

    private FileChooser fileChooser;
    private int currentBeginRange; // inclusive, starts from 1
    private int currentEndRange; // inclusive, starts from 1
    private int currentForecastHorizon;
}
