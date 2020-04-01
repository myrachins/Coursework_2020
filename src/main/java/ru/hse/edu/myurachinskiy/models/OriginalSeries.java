package ru.hse.edu.myurachinskiy.models;

import java.util.List;

public class OriginalSeries {
    public OriginalSeries(String name, List<Double> series) {
        this.name = name;
        this.series = series;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getSeries() {
        return series;
    }

    public void setSeries(List<Double> series) {
        this.series = series;
    }

    public int getSeriesSize() {
        return series.size();
    }

    private String name;
    private List<Double> series;
}
