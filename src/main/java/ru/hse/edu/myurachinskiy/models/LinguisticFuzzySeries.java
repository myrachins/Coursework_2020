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

    public List<FuzzyAffiliation> getAffiliations(double point) {
        List<FuzzyAffiliation> affiliations = new ArrayList<>();
        for (LinguisticFuzzyValue value : values) {
            affiliations.add(new FuzzyAffiliation(value.getName(), value.applyCharacteristicFunction(point)));
        }
        return affiliations;
    }

    private List<LinguisticFuzzyValue> values;
}
