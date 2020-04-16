package ru.hse.edu.myurachinskiy.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        paramToUpdate = new HashMap<>();

        currentForecastHorizon = Integer.parseInt(forecastHorizonTextField.getText());
        forecastHorizonTextField.textProperty().addListener((observable, oldValue, newValue)
                -> changeParam(() -> currentForecastHorizon = Integer.parseInt(newValue),
                forecastHorizonTextField.getId()));

        currentSubstringsNumber = Integer.parseInt(indexSubstringsNumbersTextField.getText());
        indexSubstringsNumbersTextField.textProperty().addListener((observable, oldValue, newValue)
                -> changeParam(() -> currentSubstringsNumber = Integer.parseInt(newValue),
                indexSubstringsNumbersTextField.getId()));

        seriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        seriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Integer> selected = seriesListView.getSelectionModel().getSelectedIndices();
            if (selected == null || selected.isEmpty()) { return; }

            int minSelected = Collections.min(selected);
            int maxSelected = Collections.max(selected);
            seriesListView.getSelectionModel().selectRange(minSelected, maxSelected);
            changeParam(() -> {
                currentBeginRange = minSelected + 1;
                currentEndRange = maxSelected + 1;
            }, seriesListView.getId());
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
        resizePredictListView(predictedPoints.size());
    }

    public void onIndex(ActionEvent actionEvent) {
        List<Double> distances = DataContext.fuzzyPointsSeries.index(currentBeginRange - 1, currentEndRange);
        List<Pair<Integer, Double>> indDistances = IntStream.range(0, distances.size())
                .mapToObj(i -> new Pair<>(i, distances.get(i)))
                .sorted(Comparator.comparingDouble(Pair::getValue))
                .collect(Collectors.toList());
        int substringSize = currentEndRange - currentBeginRange + 1;
        predictedListView.getItems().clear();
        for (int i = 0; i < currentSubstringsNumber; ++i) {
            int substringInd = indDistances.get(i).getKey();
            double substringDistance = indDistances.get(i).getValue();
            predictedListView.getItems().add((substringInd + 1) + ": (" + (substringInd + 1) + " - "
                    + (substringInd + substringSize) + String.format("), sim = %.5f", substringDistance));
        }
        resizePredictListView(currentSubstringsNumber);
    }

    /**
     * Changes params for prediction and index
     * @param runnable - runnable object with change (should throw IllegalArgumentException if something goes wrong)
     * @param paramId - id of JavaFx node for which update is applied
     */
    private void changeParam(Runnable runnable, String paramId) {
        try {
            paramToUpdate.put(paramId, runnable);
            for (Runnable update : paramToUpdate.values()) {
                update.run();
            }
            if (areParamsInvalid()) {
                throw new IllegalArgumentException();
            }
            predictButton.setDisable(false);
            indexButton.setDisable(false);
        } catch (IllegalArgumentException e) {
            predictButton.setDisable(true);
            indexButton.setDisable(true);
        }
    }

    private boolean areParamsInvalid() {
        int size = currentEndRange - currentBeginRange + 1;

        return currentBeginRange <= 1 || currentEndRange <= 1
                || currentBeginRange > DataContext.fuzzyPointsSeries.getSize()
                || currentEndRange > DataContext.fuzzyPointsSeries.getSize()
                || currentForecastHorizon <= 0 || size <= 0
                || size + currentForecastHorizon > currentEndRange
                || currentSubstringsNumber < 0 || currentSubstringsNumber >= currentBeginRange;
    }

    private void resizePredictListView(int listSize) {
        predictedListView.setPrefHeight(listSize * AppSettings.ROW_HEIGHT_LIST_VIEW);
    }

    @FXML
    private ListView<String> seriesListView;
    @FXML
    private TextField forecastHorizonTextField;
    @FXML
    public TextField indexSubstringsNumbersTextField;
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
    private int currentSubstringsNumber;
    private Map<String, Runnable> paramToUpdate;
}
