package ru.hse.edu.myurachinskiy.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import ru.hse.edu.myurachinskiy.models.DataContext;
import ru.hse.edu.myurachinskiy.models.LinguisticFuzzySeries;
import ru.hse.edu.myurachinskiy.models.LinguisticFuzzyValue;
import ru.hse.edu.myurachinskiy.utils.AppSettings;
import ru.hse.edu.myurachinskiy.utils.alerts.AlertFactory;
import ru.hse.edu.myurachinskiy.utils.controls.LineChartWithRectangles;
import ru.hse.edu.myurachinskiy.utils.scenes.SceneMediator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class LinguisticValuesSelectController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        yAxis.setLabel(DataContext.originalSeries.getName());
        List<Double> series = DataContext.originalSeries.getSeries();
        XYChart.Series<Double, Double> chartSeries = new XYChart.Series<>();

        for (int i = 0; i < series.size(); ++i) {
            chartSeries.getData().add(new XYChart.Data<>((double) i, series.get(i)));
        }
        seriesLineChart.getData().add(chartSeries);
        seriesLineChart.setLegendVisible(false);

        yAxis.setAutoRanging(false);
        Double min = Collections.min(series);
        Double max = Collections.max(series);
        yAxis.setLowerBound(min - 0.1 * min);
        yAxis.setUpperBound(max + 0.1 * min);
        yAxis.setTickUnit((max - min) / AppSettings.AXIS_INTERVALS_NUMBER);

        DataContext.linguisticFuzzySeries = new LinguisticFuzzySeries();

        allColors = new ArrayList<>();
        linguisticValuesListView.setCellFactory(stringListView -> new ListCell<>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item);
                    int listIndex = getIndex();
                    if (listIndex < allColors.size()) {
                        Color color = allColors.get(getIndex());
                        setStyle("-fx-background-color: " + String.format("#%02X%02X%02X%02X",
                                (int)(color.getRed() * 255), (int)(color.getGreen() * 255),
                                (int)(color.getBlue() * 255), (int)(AppSettings.COLOR_OPACITY * 255)) + ";");
                    }
                }
            }
        });
    }

    public void onDragDetectedLineChart(MouseEvent mouseEvent) {
        if (!hasStartedDragging) {
            Point2D mouseSceneCoords = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            dragStartX = (double) xAxis.getValueForDisplay(xAxis.sceneToLocal(mouseSceneCoords).getX());
        }
        hasStartedDragging = true;
    }

    public void onMouseReleasedLineChart(MouseEvent mouseEvent) {
        if (hasStartedDragging) {
            hasStartedDragging = false;
            Point2D mouseSceneCoords = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            double dragEndX = (double) xAxis.getValueForDisplay(xAxis.sceneToLocal(mouseSceneCoords).getX());
            if ((dragStartX < 0 && dragEndX < 0) || (dragStartX >= DataContext.originalSeries.getSeriesSize()
                                                    && dragEndX >= DataContext.originalSeries.getSeriesSize())) {
                Alert alert = AlertFactory.getErrorAlert("Selection error", "Selection error",
                        "Selected region should have at least one point from series");
                alert.show();
            } else {
                Pair<Integer, Integer> region = getValidRegion(dragStartX, dragEndX);
                seriesLineChart.addVerticalRangeMarker(
                        new XYChart.Data<>((double) region.getKey(), (double) region.getValue()), currentVariableColor);
                submitRegionsButton.setDisable(false);
                regions.add(new Pair<>(region.getKey(), region.getValue()));
            }
        }
    }

    public void onAddLinguisticVariable(ActionEvent actionEvent) {
        String name = linguisticVariableTextField.getText();
        if (linguisticValuesListView.getItems().contains(name)) {
            Alert alert = AlertFactory.getErrorAlert("Name error", "Name error",
                    "Names of linguistic values should be unique");
            alert.show();
        } else if (!name.isEmpty()) {
            linguisticValuesListView.getItems().add(name);
            currentLinguisticVariable = name;
            seriesLineChart.setDisable(false);
            addLinguisticVariableButton.setDisable(true);
            submitRegionsButton.setDisable(false);
            doneButton.setDisable(true);
            linguisticVariableTextField.setText("");
            regions = new ArrayList<>();
            currentVariableColor = new Color(Math.random(), Math.random(), Math.random(), 1);
            allColors.add(currentVariableColor);
            linguisticVariableTextField.setDisable(true);
        }
    }

    public void onSubmitRegions(ActionEvent actionEvent) {
        List<Double> distribution = new ArrayList<>();
        List<Double> originalSeries = DataContext.originalSeries.getSeries();
        for (Pair<Integer, Integer> region : regions) {
            for (int i = region.getKey(); i < region.getValue(); ++i) {
                distribution.add(originalSeries.get(i + 1) - originalSeries.get(i));
            }
        }
        try {
            DataContext.linguisticFuzzySeries.addValue(new LinguisticFuzzyValue(currentLinguisticVariable, distribution));
            addLinguisticVariableButton.setDisable(false);
            doneButton.setDisable(false);
            submitRegionsButton.setDisable(true);
            seriesLineChart.setDisable(true);
            linguisticVariableTextField.setDisable(false);
        } catch (IllegalArgumentException e) {
            Alert alert = AlertFactory.getErrorAlert("Selection error", "Selection error", e.getMessage());
            alert.show();
        }
    }

    public void onDone(ActionEvent actionEvent) {
        SceneMediator.changeScene(this, actionEvent);
    }

    private Pair<Integer, Integer> getValidRegion(double startX, double endX) {
        if (startX > endX) {
            double tmp = startX;
            startX = endX;
            endX = tmp;
        }
        int intStartX = Math.max((int) Math.floor(startX), 0);
        int endStartX = Math.min((int) Math.ceil(endX), DataContext.originalSeries.getSeriesSize() - 1);
        return new Pair<>(intStartX, endStartX);
    }

    @FXML
    private LineChartWithRectangles<Double, Double> seriesLineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ListView<String> linguisticValuesListView;
    @FXML
    private Button doneButton;
    @FXML
    public TextField linguisticVariableTextField;
    @FXML
    public Button addLinguisticVariableButton;
    @FXML
    public Button submitRegionsButton;

    private boolean hasStartedDragging;
    private double dragStartX;
    private String currentLinguisticVariable;
    private List<Pair<Integer, Integer>> regions;
    private Color currentVariableColor;
    private List<Color> allColors;
}
