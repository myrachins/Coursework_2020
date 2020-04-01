package ru.hse.edu.myurachinskiy.models;

import ru.hse.edu.myurachinskiy.utils.AppSettings;
import ru.hse.edu.myurachinskiy.utils.characteristics.CharacteristicStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinguisticFuzzyValue {
    public LinguisticFuzzyValue(String name, List<Double> distribution) {
        this.name = name;
        this.distribution = new ArrayList<>(distribution);
        Collections.sort(this.distribution);
        strategy = AppSettings.GET_CHARACTERISTIC_STRATEGY(this.distribution);
    }

    public String getName() {
        return name;
    }

    public List<Double> getDistribution() {
        return new ArrayList<>(distribution);
    }

    public double applyCharacteristicFunction(double point) {
        return strategy.applyCharacteristicFunction(point);
    }

    private String name;
    private List<Double> distribution;
    private CharacteristicStrategy strategy;
}
