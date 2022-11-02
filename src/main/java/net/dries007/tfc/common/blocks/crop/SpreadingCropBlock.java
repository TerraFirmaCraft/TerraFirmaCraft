/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public abstract class SpreadingCropBlock extends DefaultCropBlock
{
    public static SpreadingCropBlock create(ExtendedProperties properties, int stages, Crop crop, Supplier<Supplier<? extends Block>> fruit)
    {
        final IntegerProperty property = TFCBlockStateProperties.getAgeProperty(stages - 1);
        return new SpreadingCropBlock(properties, stages - 1, TFCBlocks.DEAD_CROPS.get(crop), TFCItems.CROP_SEEDS.get(crop), crop.getPrimaryNutrient(), ClimateRanges.CROPS.get(crop), fruit)
        {
            @Override
            public IntegerProperty getAgeProperty()
            {
                return property;
            }
        };
    }

    private final Supplier<Supplier<? extends Block>> fruit;

    protected SpreadingCropBlock(ExtendedProperties properties, int maxAge, Supplier<? extends Block> dead, Supplier<? extends Item> seeds, FarmlandBlockEntity.NutrientType primaryNutrient, Supplier<ClimateRange> climateRange, Supplier<Supplier<? extends Block>> fruit)
    {
        super(properties, maxAge, dead, seeds, primaryNutrient, climateRange);
        this.fruit = fruit;
    }

    @Override
    public float getGrowthLimit(Level level, BlockPos pos, BlockState state)
    {
        int fruitAround = 0;
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        final Block fruit = getFruit();
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            mutable.setWithOffset(pos, d);
            BlockState offsetState = level.getBlockState(mutable);
            if (Helpers.isBlock(offsetState, fruit))
            {
                fruitAround++;
                if (fruitAround > 2)
                {
                    return 0.9f;
                }
            }
        }
        return super.getGrowthLimit(level, pos, state);
    }

    @Override
    protected void postGrowthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        super.postGrowthTick(level, pos, state, crop);
        if (crop.getGrowth() >= 1)
        {
            final Direction offset = Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom());
            final BlockPos fruitPos = pos.relative(offset);
            final Block fruitBlock = getFruit();
            final BlockState fruitState = fruitBlock.defaultBlockState();
            if (fruitState.canSurvive(level, fruitPos) && level.getBlockState(fruitPos).getMaterial().isReplaceable())
            {
                level.setBlockAndUpdate(fruitPos, fruitState);
                if (level.getBlockEntity(fruitPos) instanceof DecayingBlockEntity decaying)
                {
                    decaying.setStack(new ItemStack(fruitBlock));
                }
                crop.setGrowth(Mth.nextFloat(level.getRandom(), 0.8f, 0.87f));
            }
        }
    }

    public Block getFruit()
    {
        return fruit.get().get();
    }
}
