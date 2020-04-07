package ru.hse.edu.myurachinskiy.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import ru.hse.edu.myurachinskiy.models.DataContext;
import ru.hse.edu.myurachinskiy.models.FuzzyAffiliation;

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
    }

    @FXML
    public ListView<String> seriesListView;
}
