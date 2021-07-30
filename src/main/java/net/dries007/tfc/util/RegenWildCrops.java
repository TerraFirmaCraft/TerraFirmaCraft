package net.dries007.tfc.util;


import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenWildCrops;

@ParametersAreNonnullByDefault
public class RegenWildCrops extends WorldGenWildCrops
{
    @Override
    protected boolean isValidPosition(World world, BlockPos pos)
    {
        //Modified to allow replacement of grass during spring regen
        Block test = world.getBlockState(pos).getBlock();
        return (test instanceof BlockShortGrassTFC || test.isAir(world.getBlockState(pos), world, pos) && BlocksTFC.isSoil(world.getBlockState(pos.down())));
    }
}
