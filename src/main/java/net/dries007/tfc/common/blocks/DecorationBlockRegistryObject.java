/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.registries.RegistryObject;

/**
 * A triple of {@link RegistryObject}s for slabs, stairs, and walls
 */
public record DecorationBlockRegistryObject(RegistryObject<? extends SlabBlock> slab, RegistryObject<? extends StairBlock> stair, RegistryObject<? extends WallBlock> wall) {}
