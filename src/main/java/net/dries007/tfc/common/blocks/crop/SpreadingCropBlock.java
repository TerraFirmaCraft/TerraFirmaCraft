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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.HorizontalPipeBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public abstract class SpreadingCropBlock extends DefaultCropBlock implements HorizontalPipeBlock
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
        registerDefaultState(getStateDefinition().any().setValue(NORTH, false).setValue(WEST, false).setValue(EAST, false).setValue(SOUTH, false).setValue(getAgeProperty(), 0));
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
            final BlockState growingOn = level.getBlockState(fruitPos.below());
            if (Helpers.isBlock(growingOn, TFCTags.Blocks.SPREADING_FRUIT_GROWS_ON) && level.getBlockState(fruitPos).canBeReplaced())
            {
                level.setBlockAndUpdate(fruitPos, fruitState);
                if (level.getBlockEntity(fruitPos) instanceof DecayingBlockEntity decaying)
                {
                    decaying.setStack(new ItemStack(fruitBlock));
                }
                crop.setGrowth(Mth.nextFloat(level.getRandom(), 0.6f, 0.72f));
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        state = super.updateShape(state, facing, facingState, level, pos, facingPos);
        return Helpers.setProperty(state, PROPERTY_BY_DIRECTION.get(facing), facingState.getBlock() == fruit.get().get());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return WildSpreadingCropBlock.updateBlockState(context.getLevel(), context.getClickedPos(), super.getStateForPlacement(context), getFruit());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NORTH, SOUTH, EAST, WEST));
    }

    public Block getFruit()
    {
        return fruit.get().get();
    }
}
