/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.plant.PlantRegrowth;
import net.dries007.tfc.util.registry.RegistrySoilVariant;

public class ConnectedDuffBlock extends ConnectedGrassBlock
{
    public ConnectedDuffBlock(Properties properties, Supplier<? extends Block> dirt, @Nullable Supplier<? extends Block> path, @Nullable Supplier<? extends Block> farmland)
    {
        super(properties, dirt, path, farmland);
    }

    ConnectedDuffBlock(Properties properties, SoilBlockType dirtType, RegistrySoilVariant variant)
    {
        this(properties, variant.getBlock(dirtType), variant.getBlock(SoilBlockType.GRASS_PATH), variant.getBlock(SoilBlockType.FARMLAND));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (!canBeGrass(state, level, pos))
        {
            if (level.isAreaLoaded(pos, 3))
            {
                // Turn to not-grass
                level.setBlockAndUpdate(pos, getDirt());
            }
        }
        else
        {
            PlantRegrowth.placeRisingRock(level, pos.above(), random);
        }
    }
}