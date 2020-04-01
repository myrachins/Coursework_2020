package ru.hse.edu.myurachinskiy.utils;

import ru.hse.edu.myurachinskiy.utils.characteristics.*;

import java.util.List;

public class AppSettings {
    private AppSettings() { }

    public static final String[] SEPARATORS = {",", "\\t", ";"};
    public static final double START_WINDOW_WIDTH = 1000;
    public static final double START_WINDOW_HEIGHT = 700;
    public static final int AXIS_INTERVALS_NUMBER = 10;

    public static CharacteristicStrategy GET_CHARACTERISTIC_STRATEGY(List<Double> distribution) {
        return new HistogramCharacteristic(distribution);
    }
}
