package ru.hse.edu.myurachinskiy.models;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FuzzyPointTest {
    @Test
    public void testFuzzyPointDistance() {
        List<FuzzyAffiliation> affiliations1 = new ArrayList<>();
        affiliations1.add(new FuzzyAffiliation("a", 1));
        affiliations1.add(new FuzzyAffiliation("b", 1));
        affiliations1.add(new FuzzyAffiliation("c", 0));
        FuzzyPoint fuzzyPoint1 = new FuzzyPoint(affiliations1);

        List<FuzzyAffiliation> affiliations2 = new ArrayList<>();
        affiliations2.add(new FuzzyAffiliation("a", 0));
        affiliations2.add(new FuzzyAffiliation("b", 1));
        affiliations2.add(new FuzzyAffiliation("c", 2));
        FuzzyPoint fuzzyPoint2 = new FuzzyPoint(affiliations2);

        Assert.assertEquals(fuzzyPoint1.distance(fuzzyPoint2), fuzzyPoint2.distance(fuzzyPoint1), 1e-6);
        Assert.assertEquals(fuzzyPoint1.distance(fuzzyPoint2), 5, 1e-6);
    }

    @Test
    public void testFuzzyPointZeroDistance() {
        List<FuzzyAffiliation> affiliations1 = new ArrayList<>();
        affiliations1.add(new FuzzyAffiliation("a", 1));
        affiliations1.add(new FuzzyAffiliation("b", 1));
        affiliations1.add(new FuzzyAffiliation("c", 0));
        FuzzyPoint fuzzyPoint1 = new FuzzyPoint(affiliations1);
        FuzzyPoint fuzzyPoint2 = new FuzzyPoint(affiliations1);

        Assert.assertEquals(fuzzyPoint1.distance(fuzzyPoint2), 0, 1e-6);
    }
}
