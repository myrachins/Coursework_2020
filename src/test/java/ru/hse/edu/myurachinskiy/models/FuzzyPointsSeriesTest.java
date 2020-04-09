package ru.hse.edu.myurachinskiy.models;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FuzzyPointsSeriesTest {
    @Test
    public void testSimplePredict() {
        List<FuzzyAffiliation> affiliations = new ArrayList<>();
        affiliations.add(new FuzzyAffiliation("a", 1));
        affiliations.add(new FuzzyAffiliation("b", 1));
        affiliations.add(new FuzzyAffiliation("c", 0));

        FuzzyPointsSeries fuzzyPointsSeries = new FuzzyPointsSeries();
        for (int i = 0; i < 10; ++i) {
            fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations));
        }

        for (int i = 0; i < 6; ++i) {
            Assert.assertEquals(fuzzyPointsSeries.predict(i + 1).toString(), (new FuzzyPoint(affiliations)).toString());
        }
    }

    @Test
    public void testPredict1() {
        FuzzyPointsSeries fuzzyPointsSeries = new FuzzyPointsSeries();

        List<FuzzyAffiliation> affiliations1 = new ArrayList<>();
        affiliations1.add(new FuzzyAffiliation("a", 1));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations1));

        List<FuzzyAffiliation> affiliations2 = new ArrayList<>();
        affiliations2.add(new FuzzyAffiliation("a", 0));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations2));

        List<FuzzyAffiliation> affiliations3 = new ArrayList<>();
        affiliations3.add(new FuzzyAffiliation("a", 1));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations3));

        List<FuzzyAffiliation> affiliations4 = new ArrayList<>();
        affiliations4.add(new FuzzyAffiliation("a", 0));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations4));

        Assert.assertEquals(fuzzyPointsSeries.predict(2).toString(), "(a, 0.75000)");
    }

    @Test
    public void testPredict2() {
        FuzzyPointsSeries fuzzyPointsSeries = new FuzzyPointsSeries();

        List<FuzzyAffiliation> affiliations1 = new ArrayList<>();
        affiliations1.add(new FuzzyAffiliation("a", 1));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations1));

        List<FuzzyAffiliation> affiliations2 = new ArrayList<>();
        affiliations2.add(new FuzzyAffiliation("a", 0));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations2));

        List<FuzzyAffiliation> affiliations3 = new ArrayList<>();
        affiliations3.add(new FuzzyAffiliation("a", 1));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations3));

        List<FuzzyAffiliation> affiliations4 = new ArrayList<>();
        affiliations4.add(new FuzzyAffiliation("a", 1));
        fuzzyPointsSeries.addPoint(new FuzzyPoint(affiliations4));

        Assert.assertEquals(fuzzyPointsSeries.predict(2).toString(), "(a, 1.00000)");
    }
}
