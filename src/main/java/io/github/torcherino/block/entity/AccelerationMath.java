/*
 * SPDX-FileCopyrightText: 2026 minecraft-naka contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.torcherino.block.entity;

final class AccelerationMath {
    private static final double BLOCKS_PER_SECTION = 16.0 * 16.0 * 16.0;

    private AccelerationMath() {
    }

    static int extraBlockEntityTicks(int multiplier) {
        return Math.max(0, multiplier - 1);
    }

    static int extraRandomTicks(int multiplier, int randomTickSpeed, double randomSample) {
        double expectedTicks = expectedExtraRandomTicks(multiplier, randomTickSpeed);
        int guaranteedTicks = (int) Math.min(Math.floor(expectedTicks), Integer.MAX_VALUE);
        double fractionalTick = expectedTicks - guaranteedTicks;
        return guaranteedTicks + (randomSample < fractionalTick ? 1 : 0);
    }

    static double expectedExtraRandomTicks(int multiplier, int randomTickSpeed) {
        int extraMultiplier = Math.max(0, multiplier - 1);
        int effectiveRandomTickSpeed = Math.max(0, randomTickSpeed);
        return (double) extraMultiplier * effectiveRandomTickSpeed / BLOCKS_PER_SECTION;
    }
}
