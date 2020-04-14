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

    /**
     * Predicts next forecastHorizon points based on [begin, end) points
     * @param begin - inclusive begin index
     * @param end - not inclusive end index
     * @param forecastHorizon - forecast horizon
     * @return forecastHorizon predictions
     */
    public List<FuzzyPoint> predict(int begin, int end, int forecastHorizon) {
        testRange(begin, end);
        int size = end - begin;
        if (forecastHorizon <= 0 || forecastHorizon + size > end) {
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

        for (int i = 0; i < end - size - forecastHorizon + 1; ++i) {
            double invDistance = 1 / (distance(i, begin, size) + 1);
            cumulativeInvDistance += invDistance;
            for (int offset = 0; offset < forecastHorizon; ++offset) {
                FuzzyPoint nextPoint = new FuzzyPoint(points.get(i + size + offset));
                nextPoint.multiply(invDistance);
                predicted.get(offset).add(nextPoint);
            }
        }
        for (int i = 0; i < forecastHorizon; ++i) {
            predicted.get(i).multiply(1 / cumulativeInvDistance);
        }
        return predicted;
    }

    public List<Double> index(int begin, int end) {
        testRange(begin, end);
        int size = end - begin;
        List<Double> distances = new ArrayList<>();

        for (int i = 0; i < end - size; ++i) {
            double distance = distance(i, begin, size);
            distances.add(distance);
        }
        return distances;
    }

    private double distance(int start1, int start2, int size) {
        double sumDistance = 0;

        for (int i = 0; i < size; ++i) {
            sumDistance += points.get(start1 + i).distance(points.get(start2 + i));
        }
        return sumDistance;
    }

    private void testRange(int begin, int end) {
        int size = end - begin;
        if (begin <= 0 || end <= 0 || begin >= points.size() || end > points.size()
                || size >= points.size() || size <= 0) {
            throw new IllegalArgumentException("Illegal values for begin and end");
        }
    }

    private List<FuzzyPoint> points;
}
