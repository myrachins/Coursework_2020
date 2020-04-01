package ru.hse.edu.myurachinskiy.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ru.hse.edu.myurachinskiy.models.DataContext;
import ru.hse.edu.myurachinskiy.utils.AppSettings;
import ru.hse.edu.myurachinskiy.utils.controls.LineChartWithRectangles;

import java.net.URL;
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
            seriesLineChart.addVerticalRangeMarker(new XYChart.Data<>(dragStartX, dragEndX));
        }
    }

    public void onAddLinguisticVariable(ActionEvent actionEvent) {
        String name = linguisticVariableTextField.getText();
        if (!name.isEmpty()) {
            linguisticValuesListView.getItems().add(name);
            currentLinguisticVariable = name;
            seriesLineChart.setDisable(false);
            addLinguisticVariableButton.setDisable(true);
            submitRegionsButton.setDisable(false);
            linguisticVariableTextField.setText("");
        }
    }

    public void onSubmitRegions(ActionEvent actionEvent) {
        addLinguisticVariableButton.setDisable(false);
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

}
