package ru.hse.edu.myurachinskiy.models;

import java.util.*;
import java.util.stream.Collectors;

public class FuzzyPoint {
    public FuzzyPoint(List<FuzzyAffiliation> affiliations) {
        this.affiliations = new HashMap<>();
        for (FuzzyAffiliation affiliation : affiliations) {
            this.affiliations.put(affiliation.getLinguisticValue(), affiliation.getAffiliationDegree());
        }
    }

    public FuzzyPoint(FuzzyPoint fuzzyPoint) {
        this.affiliations = new HashMap<>(fuzzyPoint.affiliations);
    }

    public double distance(FuzzyPoint other) {
        checkSizes(other);
        double distance = 0;

        for (String linguisticValue : affiliations.keySet()) {
            double diff = affiliations.get(linguisticValue) - other.affiliations.get(linguisticValue);
            distance += diff * diff;
        }
        return distance;
    }

    public void add(FuzzyPoint other) {
        checkSizes(other);

        for (String linguisticValue : affiliations.keySet()) {
            affiliations.put(linguisticValue, affiliations.get(linguisticValue) + other.affiliations.get(linguisticValue));
        }
    }

    public void multiply(double x) {
        affiliations.replaceAll((k, v) -> affiliations.get(k) * x);
    }

    public Set<String> getLinguisticValues() {
        return affiliations.keySet();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        List<FuzzyAffiliation> sourceAffiliations = affiliations.entrySet()
                .stream()
                .map(entry -> new FuzzyAffiliation(entry.getKey(), entry.getValue()))
                .sorted((lhs, rhs) -> -Double.compare(lhs.getAffiliationDegree(), rhs.getAffiliationDegree()))
                .collect(Collectors.toList());

        for (int i = 0; i < sourceAffiliations.size(); i++) {
            if (i != 0) {
                stringBuilder.append("; ");
            }
            FuzzyAffiliation affiliation = sourceAffiliations.get(i);
            stringBuilder.append("(");
            stringBuilder.append(affiliation.getLinguisticValue());
            stringBuilder.append(", ");
            stringBuilder.append(String.format("%.5f", affiliation.getAffiliationDegree()));
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }

    private void checkSizes(FuzzyPoint other) {
        if (affiliations.size() != other.affiliations.size()) {
            throw new IllegalArgumentException("Wrong number of linguistic values");
        }
    }

    private Map<String, Double> affiliations;
}
