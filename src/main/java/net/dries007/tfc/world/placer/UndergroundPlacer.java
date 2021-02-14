/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;

public class UndergroundPlacer extends BlockPlacer
{
    public static final Codec<UndergroundPlacer> CODEC = Codec.unit(new UndergroundPlacer());

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState state, Random random)
    {
        if (worldIn.getBlockState(pos).getBlock() == Blocks.CAVE_AIR && worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) > pos.getY())
        {
            worldIn.setBlockState(pos, state, 3);
        }
    }

    @Override
    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.UNDERGROUND.get();
    }
}
