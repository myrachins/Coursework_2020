package ru.hse.edu.myurachinskiy.models;

import java.util.ArrayList;
import java.util.List;

public class LinguisticFuzzySeries {
    public LinguisticFuzzySeries() {
        this.values = new ArrayList<>();
    }

    public List<LinguisticFuzzyValue> getValues() {
        return values;
    }

    public void addValue(LinguisticFuzzyValue value) {
        values.add(value);
    }

    private List<LinguisticFuzzyValue> values;
}
