package ru.hse.edu.myurachinskiy.utils.characteristics;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HistogramCharacteristicTest {
    @Test
    public void testDistribution() {
        List<Double> distributionElements = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            distributionElements.add(random.nextDouble() * 2 + 3);
        }
        HistogramCharacteristic characteristic = new HistogramCharacteristic(distributionElements);
        List<Double> xElements = new ArrayList<>();
        List<Double> yElements = new ArrayList<>();
        for (double i = -10; i < 10; i += 1e-2) {
            xElements.add(i);
            yElements.add(characteristic.applyCharacteristicFunction(i));
        }
        // Data for python visualization
        System.out.print("x = [");
        for (Double xElement : xElements) {
            System.out.print(String.format("%.5f, ", xElement));
        }
        System.out.println("]");
        System.out.print("y = [");
        for (Double yElement : yElements) {
            System.out.print(String.format("%.5f, ", yElement));
        }
        System.out.print("]");
    }

    private static Random random = new Random();
}
