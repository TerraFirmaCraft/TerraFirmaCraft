/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * A triple of {@link DeferredHolder}s for slabs, stairs, and walls
 */
public record DecorationBlockHolder(
    TFCBlocks.Id<? extends SlabBlock> slab,
    TFCBlocks.Id<? extends StairBlock> stair,
    TFCBlocks.Id<? extends WallBlock> wall
) {}
