package ru.hse.edu.myurachinskiy.models;

import javafx.geometry.HPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FuzzyPointsSeries {
    public FuzzyPointsSeries() {
        points = new ArrayList<>();
    }

    public int getSize() {
        return points.size();
    }

    public void addPoint(FuzzyPoint point) {
        points.add(point);
    }

    public List<FuzzyPoint> predict(int tailShift, int forecastHorizon) {
        if (tailShift <= 0 || tailShift >= points.size()) {
            throw new IllegalArgumentException("Illegal value for tailShift");
        }
        if (forecastHorizon <= 0 || forecastHorizon + tailShift > points.size()) {
            throw new IllegalArgumentException("Illegal value for forecastHorizon");
        }
        Set<String> linguisticValues = points.get(0).getLinguisticValues();
        List<FuzzyAffiliation> affiliations = linguisticValues.stream()
                .map(value -> new FuzzyAffiliation(value, 0))
                .collect(Collectors.toList());
        List<FuzzyPoint> predicted = new ArrayList<>();
        for (int i = 0; i < forecastHorizon; ++i) {
            predicted.add(new FuzzyPoint(new ArrayList<>(affiliations)));
        }
        double cumulativeInvDistance = 0;

        for (int i = 0; i < points.size() - tailShift - forecastHorizon + 1; ++i) {
            double invDistance = 1 / (distance(i,points.size() - tailShift, tailShift) + 1);
            cumulativeInvDistance += invDistance;
            for (int offset = 0; offset < forecastHorizon; ++offset) {
                FuzzyPoint nextPoint = new FuzzyPoint(points.get(i + tailShift + offset));
                nextPoint.multiply(invDistance);
                predicted.get(offset).add(nextPoint);
            }
        }
        for (int i = 0; i < forecastHorizon; ++i) {
            predicted.get(i).multiply(1 / cumulativeInvDistance);
        }
        return predicted;
    }

    public int index(int tailShift) {
        if (tailShift <= 0 || tailShift >= points.size()) {
            throw new IllegalArgumentException("Illegal value for tailShift");
        }
        double minDistance = -1;
        int minDistanceIndex = -1;

        for (int i = 0; i < points.size() - tailShift; ++i) {
            double distance = distance(i, points.size() - tailShift, tailShift);
            if (minDistance == -1 || distance < minDistance) {
                minDistance = distance;
                minDistanceIndex = i;
            }
        }
        return minDistanceIndex;
    }

    private double distance(int start1, int start2, int size) {
        double sumDistance = 0;

        for (int i = 0; i < size; ++i) {
            sumDistance += points.get(start1 + i).distance(points.get(start2 + i));
        }
        return sumDistance;
    }

    private List<FuzzyPoint> points;
}
