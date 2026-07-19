/*
 * SPDX-FileCopyrightText: 2026 minecraft-naka contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.torcherino.block.entity;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccelerationMathTest {
    @Test
    void blockEntityTicksProduceTrueTotalMultipliers() {
        assertEquals(0, AccelerationMath.extraBlockEntityTicks(1));
        assertEquals(3, AccelerationMath.extraBlockEntityTicks(4));
        assertEquals(7, AccelerationMath.extraBlockEntityTicks(8));
        assertEquals(15, AccelerationMath.extraBlockEntityTicks(16));
        assertEquals(31, AccelerationMath.extraBlockEntityTicks(32));
    }

    @Test
    void oneTimesDoesNotForceRandomTicks() {
        assertEquals(0.0, AccelerationMath.expectedExtraRandomTicks(1, 3));
        assertEquals(0, AccelerationMath.extraRandomTicks(1, 3, 0.0));
    }

    @Test
    void randomTickExpectationUsesVanillaSectionProbability() {
        assertEquals(9.0 / 4096.0, AccelerationMath.expectedExtraRandomTicks(4, 3));
        assertEquals(93.0 / 4096.0, AccelerationMath.expectedExtraRandomTicks(32, 3));
    }

    @Test
    void fractionalRandomTickExpectationIsSampled() {
        assertEquals(1, AccelerationMath.extraRandomTicks(32, 3, 0.0));
        assertEquals(0, AccelerationMath.extraRandomTicks(32, 3, 0.5));
    }

    @Test
    void highRandomTickSpeedCanProduceMultipleExtraTicks() {
        assertEquals(3, AccelerationMath.extraRandomTicks(4, 4096, 0.5));
        assertEquals(4, AccelerationMath.extraRandomTicks(4, 5000, 0.1));
        assertEquals(3, AccelerationMath.extraRandomTicks(4, 5000, 0.9));
    }

    @Test
    void sampledRandomTickFrequencyTracksRequestedMultipliers() {
        int samples = 1_000_000;
        int randomTickSpeed = 3;
        Random random = new Random(0x544F524348L);

        for (int multiplier : new int[]{1, 4, 8, 16, 32}) {
            int observedExtraTicks = 0;
            for (int sample = 0; sample < samples; sample++) {
                observedExtraTicks += AccelerationMath.extraRandomTicks(
                        multiplier, randomTickSpeed, random.nextDouble());
            }

            double expectedExtraTicks = samples
                    * AccelerationMath.expectedExtraRandomTicks(multiplier, randomTickSpeed);
            double tolerance = Math.max(1.0, 6.0 * Math.sqrt(expectedExtraTicks));
            assertEquals(expectedExtraTicks, observedExtraTicks, tolerance);
        }
    }
}
