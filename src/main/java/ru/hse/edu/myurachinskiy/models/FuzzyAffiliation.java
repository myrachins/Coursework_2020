package ru.hse.edu.myurachinskiy.models;

public class FuzzyAffiliation {
    public FuzzyAffiliation(String linguisticValue, double affiliationDegree) {
        this.linguisticValue = linguisticValue;
        this.affiliationDegree = affiliationDegree;
    }

    public String getLinguisticValue() {
        return linguisticValue;
    }

    public double getAffiliationDegree() {
        return affiliationDegree;
    }

    private String linguisticValue;
    private double affiliationDegree;
}
