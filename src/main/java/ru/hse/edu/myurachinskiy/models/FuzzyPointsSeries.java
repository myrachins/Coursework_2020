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

    public FuzzyPoint predict(int tailShift) {
        if (tailShift <= 0 || tailShift >= points.size()) {
            throw new IllegalArgumentException("Illegal value for tailShift");
        }
        Set<String> linguisticValues = points.get(0).getLinguisticValues();
        List<FuzzyAffiliation> affiliations = linguisticValues.stream()
                .map(value -> new FuzzyAffiliation(value, 0))
                .collect(Collectors.toList());
        FuzzyPoint predicted = new FuzzyPoint(affiliations);
        double cumulativeInvDistance = 0;

        for (int i = 0; i < points.size() - tailShift; ++i) {
            double invDistance = 1 / (distance(i,points.size() - tailShift, tailShift) + 1);
            FuzzyPoint nextPoint = new FuzzyPoint(points.get(i + tailShift));

            cumulativeInvDistance += invDistance;
            nextPoint.multiply(invDistance);
            predicted.add(nextPoint);
        }
        predicted.multiply(1 / cumulativeInvDistance);

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
