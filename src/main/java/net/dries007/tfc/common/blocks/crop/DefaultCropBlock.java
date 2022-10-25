/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.registry.RegistryCrop;

public abstract class DefaultCropBlock extends CropBlock
{
    public static DefaultCropBlock create(ExtendedProperties properties, int stages, RegistryCrop crop)
    {
        final IntegerProperty property = TFCBlockStateProperties.getAgeProperty(stages - 1);
        return new DefaultCropBlock(properties, stages - 1, crop)
        {
            @Override
            public IntegerProperty getAgeProperty()
            {
                return property;
            }
        };
    }

    protected DefaultCropBlock(ExtendedProperties properties, int maxAge, RegistryCrop crop)
    {
        super(properties, maxAge, crop);
    }

    @Override
    public void die(Level level, BlockPos pos, BlockState state, boolean fullyGrown)
    {
        final BlockState deadState = dead.get().defaultBlockState().setValue(DeadCropBlock.MATURE, fullyGrown);
        level.setBlockAndUpdate(pos, deadState);
    }

    @Override
    protected void postGrowthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        final int age = crop.getGrowth() == 1 ? getMaxAge() : (int) (crop.getGrowth() * getMaxAge());
        level.setBlockAndUpdate(pos, state.setValue(getAgeProperty(), age));
    }
}