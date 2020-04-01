package ru.hse.edu.myurachinskiy.utils.characteristics;

import java.util.List;

public abstract class CharacteristicStrategy {
    CharacteristicStrategy(List<Double> distribution) {
        if (distribution.size() < 2) {
            throw new IllegalArgumentException("Size of distribution should be at least 2. Now it's "
                    + distribution.size());
        }
        this.distribution = distribution;
    }

    public abstract double applyCharacteristicFunction(double point);

    protected List<Double> distribution;
}
