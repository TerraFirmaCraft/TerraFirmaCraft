/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import net.dries007.tfc.common.blocks.wood.Wood;

/**
 * Interface for use in {@link Wood.BlockType} registration calls.
 */
public interface RegistryWood extends StringRepresentable
{
    MapColor woodColor();

    MapColor barkColor();

    TreeGrower tree();

    Supplier<Integer> ticksToGrow();

    /**
     * @return The vertical coordinate (from 0-255) on the foliage_fall colormap for this wood type's leaves.
     */
    int autumnIndex();

    /**
     * @return A block of this wood, of the provided type. It is <strong>not necessary</strong> to implement this for every type, only the ones that are needed.
     */
    Supplier<Block> getBlock(Wood.BlockType type);

    BlockSetType getBlockSet();

    WoodType getVanillaWoodType();
}
