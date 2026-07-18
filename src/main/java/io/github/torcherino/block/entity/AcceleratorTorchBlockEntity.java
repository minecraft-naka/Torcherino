/*
 * SPDX-FileCopyrightText: 2026 minecraft-naka contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.torcherino.block.entity;

import io.github.torcherino.Torcherino;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class AcceleratorTorchBlockEntity extends BlockEntity {
    public static final TagKey<Block> BLOCK_BLACKLIST =
            TagKey.create(Registries.BLOCK, Torcherino.id("acceleration_blacklist"));
    public static final TagKey<BlockEntityType<?>> BLOCK_ENTITY_BLACKLIST =
            TagKey.create(Registries.BLOCK_ENTITY_TYPE, Torcherino.id("acceleration_blacklist"));

    private static final int[] RADII = {0, 1, 2, 3, 4};
    private static final int[] SPEED_MULTIPLIERS = {1, 4, 8, 16, 32};
    private static final int MAX_OPERATIONS_PER_TICK = 16384;

    private int rangeIndex = 1;
    private int speedIndex = 1;

    public AcceleratorTorchBlockEntity(BlockPos pos, BlockState state) {
        super(Torcherino.ACCELERATOR_TORCH_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(
            Level level, BlockPos torchPos, BlockState torchState, AcceleratorTorchBlockEntity accelerator) {
        if (!(level instanceof ServerLevel serverLevel)
                || accelerator.rangeIndex == 0
                || serverLevel.hasNeighborSignal(torchPos)) {
            return;
        }

        accelerator.accelerate(serverLevel, torchPos);
    }

    private void accelerate(ServerLevel level, BlockPos torchPos) {
        int radius = RADII[rangeIndex];
        int extraTicks = SPEED_MULTIPLIERS[speedIndex];
        int operations = 0;
        BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos();

        for (int y = torchPos.getY() - 1; y <= torchPos.getY() + 1; y++) {
            for (int x = torchPos.getX() - radius; x <= torchPos.getX() + radius; x++) {
                for (int z = torchPos.getZ() - radius; z <= torchPos.getZ() + radius; z++) {
                    if (operations >= MAX_OPERATIONS_PER_TICK) {
                        return;
                    }

                    target.set(x, y, z);
                    if (target.equals(torchPos)
                            || !level.isInWorldBounds(target)
                            || !level.getChunkSource().hasChunk(target.getX() >> 4, target.getZ() >> 4)) {
                        continue;
                    }

                    BlockState targetState = level.getBlockState(target);
                    if (targetState.isAir() || targetState.is(BLOCK_BLACKLIST) || targetState.is(Torcherino.ACCELERATOR_TORCH)) {
                        continue;
                    }

                    if (targetState.isRandomlyTicking()) {
                        for (int tick = 0; tick < extraTicks && operations < MAX_OPERATIONS_PER_TICK; tick++) {
                            if (level.getBlockState(target) != targetState) {
                                break;
                            }
                            targetState.randomTick(level, target.immutable(), level.getRandom());
                            operations++;
                        }
                    }

                    BlockEntity targetEntity = level.getBlockEntity(target);
                    if (targetEntity == null
                            || targetEntity.isRemoved()
                            || targetEntity.typeHolder().is(BLOCK_ENTITY_BLACKLIST)) {
                        continue;
                    }

                    for (int tick = 0; tick < extraTicks && operations < MAX_OPERATIONS_PER_TICK; tick++) {
                        if (targetEntity.isRemoved()
                                || level.getBlockEntity(target) != targetEntity
                                || level.getBlockState(target) != targetState) {
                            break;
                        }
                        if (!tickBlockEntity(level, target.immutable(), targetState, targetEntity)) {
                            break;
                        }
                        operations++;
                    }
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean tickBlockEntity(
            ServerLevel level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        BlockEntityTicker ticker = state.getTicker(level, blockEntity.getType());
        if (ticker == null) {
            return false;
        }
        ticker.tick(level, pos, state, blockEntity);
        return true;
    }

    public void cycleRange() {
        rangeIndex = (rangeIndex + 1) % RADII.length;
        setChanged();
    }

    public void cycleSpeed() {
        speedIndex = (speedIndex + 1) % SPEED_MULTIPLIERS.length;
        setChanged();
    }

    public void showStatus(Player player) {
        Component range = rangeIndex == 0
                ? Component.translatable("message.torcherino.range.off")
                : Component.translatable(
                        "message.torcherino.range.area",
                        RADII[rangeIndex] * 2 + 1,
                        3,
                        RADII[rangeIndex] * 2 + 1);
        Component state = level != null && level.hasNeighborSignal(worldPosition)
                ? Component.translatable("message.torcherino.state.paused")
                : Component.translatable("message.torcherino.state.active");
        player.sendOverlayMessage(
                Component.translatable("message.torcherino.status", range, SPEED_MULTIPLIERS[speedIndex], state));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        rangeIndex = Mth.clamp(input.getIntOr("range", 1), 0, RADII.length - 1);
        speedIndex = Mth.clamp(input.getIntOr("speed", 1), 0, SPEED_MULTIPLIERS.length - 1);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("range", rangeIndex);
        output.putInt("speed", speedIndex);
    }
}
