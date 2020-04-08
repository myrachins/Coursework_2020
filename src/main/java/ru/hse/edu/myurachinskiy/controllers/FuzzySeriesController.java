package ru.hse.edu.myurachinskiy.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.edu.myurachinskiy.models.DataContext;
import ru.hse.edu.myurachinskiy.models.FuzzyAffiliation;
import ru.hse.edu.myurachinskiy.utils.alerts.AlertFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FuzzySeriesController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Double> series = DataContext.originalSeries.getSeries();
        for (int i = 0; i < series.size() - 1; i++) {
            double point = series.get(i + 1) - series.get(i);
            List<FuzzyAffiliation> affiliations = DataContext.linguisticFuzzySeries.getAffiliations(point);
            affiliations.sort((lhs, rhs) -> -Double.compare(lhs.getAffiliationDegree(), rhs.getAffiliationDegree()));
            StringBuilder stringBuilder = new StringBuilder(i + 1 + ") ");
            for (int j = 0; j < affiliations.size(); j++) {
                if (j != 0) {
                    stringBuilder.append("; ");
                }
                FuzzyAffiliation affiliation = affiliations.get(j);
                stringBuilder.append("(");
                stringBuilder.append(affiliation.getLinguisticValue());
                stringBuilder.append(", ");
                stringBuilder.append(String.format("%.5f", affiliation.getAffiliationDegree()));
                stringBuilder.append(")");
            }
            seriesListView.getItems().add(stringBuilder.toString());
        }

        fileChooser = new FileChooser();
        fileChooser.setTitle("Save time series as ...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt", "*.txt"));
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

    @FXML
    public ListView<String> seriesListView;

    private FileChooser fileChooser;
}
