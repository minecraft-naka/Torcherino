/*
 * SPDX-FileCopyrightText: 2026 minecraft-naka contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.torcherino;

import io.github.torcherino.block.AcceleratorTorchBlock;
import io.github.torcherino.block.entity.AcceleratorTorchBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Set;

public final class Torcherino implements ModInitializer {
    public static final String MOD_ID = "torcherino";
    private static final String TORCH_ID = "accelerator_torch";

    public static final ResourceKey<Block> ACCELERATOR_TORCH_KEY =
            ResourceKey.create(Registries.BLOCK, id(TORCH_ID));
    public static final ResourceKey<Item> ACCELERATOR_TORCH_ITEM_KEY =
            ResourceKey.create(Registries.ITEM, id(TORCH_ID));

    public static final AcceleratorTorchBlock ACCELERATOR_TORCH = new AcceleratorTorchBlock(
            BlockBehaviour.Properties.of()
                    .setId(ACCELERATOR_TORCH_KEY)
                    .mapColor(MapColor.WOOD)
                    .noCollision()
                    .instabreak()
                    .lightLevel(state -> 14)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY));

    public static final Item ACCELERATOR_TORCH_ITEM = new BlockItem(
            ACCELERATOR_TORCH,
            new Item.Properties().setId(ACCELERATOR_TORCH_ITEM_KEY));

    public static final BlockEntityType<AcceleratorTorchBlockEntity> ACCELERATOR_TORCH_BLOCK_ENTITY =
            new BlockEntityType<>(AcceleratorTorchBlockEntity::new, Set.of(ACCELERATOR_TORCH));

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.BLOCK, ACCELERATOR_TORCH_KEY, ACCELERATOR_TORCH);
        Registry.register(BuiltInRegistries.ITEM, ACCELERATOR_TORCH_ITEM_KEY, ACCELERATOR_TORCH_ITEM);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(TORCH_ID), ACCELERATOR_TORCH_BLOCK_ENTITY);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(entries -> entries.accept(ACCELERATOR_TORCH_ITEM));

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND
                    || !player.isShiftKeyDown()
                    || player.isSpectator()
                    || !level.getBlockState(hitResult.getBlockPos()).is(ACCELERATOR_TORCH)) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()
                    && level.getBlockEntity(hitResult.getBlockPos()) instanceof AcceleratorTorchBlockEntity accelerator) {
                accelerator.cycleSpeed();
                accelerator.showStatus(player);
            }
            return InteractionResult.SUCCESS;
        });
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
