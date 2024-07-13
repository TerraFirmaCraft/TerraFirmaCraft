/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.GlassBasinBlockEntity;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;

public class GlassBasinBlock extends DeviceBlock
{
    public static boolean isValid(Level level, BlockPos pos)
    {
        if (!level.getBlockState(pos).isAir())
        {
            return false;
        }
        for (Direction side : Helpers.DIRECTIONS)
        {
            if (side != Direction.UP)
            {
                if (!Helpers.isBlock(level.getBlockState(pos.relative(side)), TFCTags.Blocks.GLASS_BASIN_BLOCKS))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static final VoxelShape[] SHAPES = Util.make(() -> {
        final VoxelShape[] shapes = new VoxelShape[16];
        for (int i = 0; i < 16; i++)
        {
            shapes[i] = box(0, 0, 0, 16, i + 1, 16);
        }
        return shapes;
    });

    public GlassBasinBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (level.getBlockEntity(pos) instanceof GlassBasinBlockEntity basin && basin.getAnimationTicks() > -1)
        {
            return SHAPES[Mth.floor(Mth.clampedMap((float) basin.getAnimationTicks(), 0f, 60f, 0f, 15f))];
        }
        return SHAPES[0];
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (entity instanceof LivingEntity living)
        {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
