package ru.hse.edu.myurachinskiy.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.edu.myurachinskiy.models.DataContext;
import ru.hse.edu.myurachinskiy.models.OriginalSeries;
import ru.hse.edu.myurachinskiy.utils.AppSettings;
import ru.hse.edu.myurachinskiy.utils.alerts.AlertFactory;
import ru.hse.edu.myurachinskiy.utils.scenes.SceneMediator;

import java.io.*;
import java.net.URL;
import java.util.*;

public class SeriesSelectController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        separatorsComboBox.setItems(FXCollections.observableArrayList(AppSettings.SEPARATORS));
        separatorsComboBox.setValue(AppSettings.SEPARATORS[0]);

        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file with time series");
    }

    public void onSelectFile(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            fileNameTextField.setText(file.getAbsolutePath());
        }
    }

    public void onLoadFile(ActionEvent actionEvent) {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(fileNameTextField.getText()));
            final String separator = separatorsComboBox.getValue();
            String line = br.readLine();
            if (line == null) {
                throw new IllegalArgumentException("File has no content");
            }
            disableColumnSelection();
            csvColumns = line.split(separator);
            csvRows = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] row = line.split(separator);
                if (row.length != csvColumns.length) {
                    throw new IllegalArgumentException("Line \"" + line + "\" has invalid number of values");
                }
                csvRows.add(row);
            }
            if (csvRows.size() < 3) {
                throw new IllegalArgumentException("Table should have at least three rows with data");
            }
            drawTable();
            enableColumnSelection();
        } catch (FileNotFoundException e) {
            Alert alert = AlertFactory.getErrorAlert("File error", "File not found", e.getMessage());
            alert.show();
        } catch (IOException e) {
            Alert alert = AlertFactory.getErrorAlert("File error", "Error while reading file", e.getMessage());
            alert.show();
        } catch (IllegalArgumentException e) {
            Alert alert = AlertFactory.getErrorAlert("File error", "Invalid file", e.getMessage());
            alert.show();
        }
    }

    public void onDone(ActionEvent actionEvent) {
        String seriesName = numericColumnsComboBox.getValue();
        DataContext.originalSeries = new OriginalSeries(seriesName, numericColumnToData.get(seriesName));
        SceneMediator.changeScene(this, actionEvent);
    }

    private void enableColumnSelection() {
        numericColumnToData = new HashMap<>();
        for (int i = 0; i < csvColumns.length; ++i) {
            List<Double> numericValues = new ArrayList<>();
            try {
                for (String[] row : csvRows) {
                    numericValues.add(Double.parseDouble(row[i]));
                }
                numericColumnToData.put(csvColumns[i], numericValues);
            } catch (NumberFormatException e) { /* Column contains not only numeric data */}
        }
        String[] numericColumns = numericColumnToData.keySet().toArray(new String[0]);
        if (numericColumns.length == 0) {
            throw new IllegalArgumentException("No columns with numeric data");
        }
        numericColumnsComboBox.setItems(FXCollections.observableArrayList(numericColumns));
        numericColumnsComboBox.setValue(numericColumns[0]);
        numericColumnsComboBox.setDisable(false);
        doneButton.setDisable(false);
    }

    private void disableColumnSelection() {
        numericColumnsComboBox.setValue("");
        numericColumnsComboBox.setDisable(true);
        doneButton.setDisable(true);
    }

    private void drawTable() {
        csvTableView.getColumns().clear();
        csvTableView.getItems().clear();

        for (int i = 0; i < csvColumns.length; ++i) {
            csvTableView.getColumns().add(createColumn(i, csvColumns[i]));
        }
        for (String[] row : csvRows) {
            ObservableList<StringProperty> csvRow = FXCollections.observableArrayList();
            for (String value : row) {
                csvRow.add(new SimpleStringProperty(value));
            }
            csvTableView.getItems().add(csvRow);
        }
    }

    private TableColumn<ObservableList<StringProperty>, String> createColumn(final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setText(columnTitle);
        column.setCellValueFactory(cellDataFeatures -> {
            ObservableList<StringProperty> values = cellDataFeatures.getValue();
            if (columnIndex >= values.size()) {
                return new SimpleStringProperty("");
            } else {
                return cellDataFeatures.getValue().get(columnIndex);
            }
        });
        return column;
    }

    @FXML
    private TextField fileNameTextField;
    @FXML
    private ComboBox<String> separatorsComboBox;
    @FXML
    private TableView<ObservableList<StringProperty>> csvTableView;
    @FXML
    private ComboBox<String> numericColumnsComboBox;
    @FXML
    private Button doneButton;

    private FileChooser fileChooser;
    private String[] csvColumns;
    private List<String[]> csvRows;
    private HashMap<String, List<Double>> numericColumnToData;
}
