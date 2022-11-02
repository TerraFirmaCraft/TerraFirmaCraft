/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.feature.tree.TFCTreeGrower;

/**
 * Interface for use in {@link Wood.BlockType} registration calls.
 */
public interface RegistryWood extends StringRepresentable
{
    MaterialColor woodColor();

    MaterialColor barkColor();

    TFCTreeGrower tree();

    int maxDecayDistance();

    int daysToGrow();

    /**
     * @return A block of this wood, of the provided type. It is <strong>not necessary</strong> to implement this for every type, only the ones that are needed.
     */
    Supplier<Block> getBlock(Wood.BlockType type);
}
