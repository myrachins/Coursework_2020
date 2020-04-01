package ru.hse.edu.myurachinskiy.models;

import java.util.ArrayList;
import java.util.List;

public class LinguisticFuzzyValue {
    public LinguisticFuzzyValue(String name) {
        this(name, new ArrayList<>());
    }

    public LinguisticFuzzyValue(String name, List<Double> distribution) {
        this.name = name;
        this.distribution = distribution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getDistribution() {
        return distribution;
    }

    public void setDistribution(List<Double> distribution) {
        this.distribution = distribution;
    }

    private String name;
    private List<Double> distribution;
}
