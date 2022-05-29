/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FallenLeavesBlock extends GroundcoverBlock implements IForgeBlockExtension
{
    private static final VoxelShape VERY_FLAT = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    private final ExtendedProperties properties;

    public FallenLeavesBlock(ExtendedProperties properties)
    {
        super(properties, VERY_FLAT, null);
        this.properties = properties;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true; // Not for the purposes of leaf decay, but for the purposes of seasonal updates
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return VERY_FLAT;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
