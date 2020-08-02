/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.util.climate.ITemperatureBlock;

@ParametersAreNonnullByDefault
public class BlockSnowTFC extends BlockSnow implements ITemperatureBlock
{
    public BlockSnowTFC()
    {
        setHardness(0.1F);
        setSoundType(SoundType.SNOW);
        setLightOpacity(0);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        super.randomTick(worldIn, pos, state, random);
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - getLightOpacity(state, worldIn, pos) || ClimateTFC.getActualTemp(worldIn, pos) > 4f)
        {
            if (state.getValue(LAYERS) > 1)
            {
                worldIn.setBlockState(pos, state.withProperty(LAYERS, state.getValue(LAYERS) - 1));
            }
            else
            {
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!(entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).isCreative()))
        {
            entityIn.motionX *= ConfigTFC.General.MISC.snowMovementModifier;
            entityIn.motionZ *= ConfigTFC.General.MISC.snowMovementModifier;
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!(entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).isCreative()))
        {
            entityIn.motionX *= ConfigTFC.General.MISC.snowMovementModifier;
            entityIn.motionZ *= ConfigTFC.General.MISC.snowMovementModifier;
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState stateDown = worldIn.getBlockState(pos.down());
        Block block = stateDown.getBlock();

        if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER && block != BlocksTFC.SEA_ICE)
        {
            return stateDown.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID || stateDown.getBlock().isLeaves(stateDown, worldIn, pos.down()) || block == this && stateDown.getValue(LAYERS) == 8;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onTemperatureUpdateTick(World world, BlockPos pos, IBlockState state)
    {
        if (world.isRaining() && world.getLightFor(EnumSkyBlock.BLOCK, pos.up()) < 11 - getLightOpacity(state, world, pos))
        {
            int expectedLayers = -2 - (int) (ClimateTFC.getActualTemp(world, pos) * 0.5f);
            if (expectedLayers > state.getValue(LAYERS) && state.getValue(LAYERS) < 7) // If we prevent this from getting to a full block, it won't infinitely accumulate
            {
                world.setBlockState(pos, state.withProperty(LAYERS, state.getValue(LAYERS) + 1));
            }
        }
    }
}
