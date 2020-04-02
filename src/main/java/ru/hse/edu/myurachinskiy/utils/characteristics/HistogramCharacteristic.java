package ru.hse.edu.myurachinskiy.utils.characteristics;

import java.util.List;

public class HistogramCharacteristic extends CharacteristicStrategy {
    public HistogramCharacteristic(List<Double> distribution) {
        super(distribution);
        h = 1.06 * std() * Math.pow(this.distribution.size(), -0.2);
    }

    @Override
    public double applyCharacteristicFunction(double point) {
        if (h == 0) {
            return distribution.get(0) == point ? 1 : 0;
        }
        double kernelsSum = 0;
        for (Double p : distribution) {
            kernelsSum += kernel((point - p) / h);
        }
        return kernelsSum / (distribution.size() * h);
    }

    private double kernel(double x) {
        return 1 / Math.sqrt(2 * Math.PI) * Math.exp(-0.5 * x * x);
    }

    private double std() {
        double aver = 0;
        for (Double point : distribution) {
            aver += point;
        }
        aver /= distribution.size();

        double squareSum = 0;
        for (Double point : distribution) {
            double diff = point - aver;
            squareSum += diff * diff;
        }
        return Math.sqrt(squareSum / (distribution.size() - 1));
    }

    private double h;
}
