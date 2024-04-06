/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

/**
 * A triple of {@link Supplier}s for slabs, stairs, and walls
 */
public record DecorationBlockRegistryObject(Supplier<? extends SlabBlock> slab, Supplier<? extends StairBlock> stair, Supplier<? extends WallBlock> wall) {}
