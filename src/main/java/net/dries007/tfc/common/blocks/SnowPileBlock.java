/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.dries007.tfc.common.tileentity.SnowPileTileEntity;
import net.dries007.tfc.util.Helpers;

/**
 * This block is a snow layer block that hides / covers a block underneath
 * When it melts, it will transform into the underlying block, with one level of snow active
 */
public class SnowPileBlock extends SnowBlock implements IForgeBlockProperties
{
    /**
     * Converts an existing block state to a snow pile consisting of that block state
     *
     * @param world      The world
     * @param pos        The position
     * @param state      The original state
     * @param snowLayers How many layers of snow were in the original state (may be 0 if the state doesn't pile up it's own snow)
     */
    public static void convertToPile(IWorld world, BlockPos pos, BlockState state)
    {
        world.setBlockState(pos, TFCBlocks.SNOW_PILE.get().getDefaultState(), 3);
        Helpers.getTileEntityOrThrow(world, pos, SnowPileTileEntity.class).setInternalState(state);
    }

    private final ForgeBlockProperties properties;

    public SnowPileBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());

        this.properties = properties;
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    /**
     * This allows two things:
     * - Snow piles are removed one layer at a time, same as snow blocks (modified via mixin)
     * - Once removed enough, they convert to the underlying block state.
     */
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(world, pos, state, player);
        SnowPileTileEntity te = Helpers.getTileEntityOrThrow(world, pos, SnowPileTileEntity.class);
        BlockState newState = te.getDestroyedState(state);
        return world.setBlockState(pos, newState, !world.isRemote ? 11 : 3);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(Blocks.SNOW);
    }
}
