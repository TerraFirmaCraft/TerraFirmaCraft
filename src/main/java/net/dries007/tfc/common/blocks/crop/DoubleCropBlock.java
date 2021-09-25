package net.dries007.tfc.common.blocks.crop;

import java.util.Locale;
import java.util.function.Supplier;


import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public abstract class DoubleCropBlock extends CropBlock
{
    public static final EnumProperty<Part> PART = TFCBlockStateProperties.DOUBLE_CROP_PART;

    public static DoubleCropBlock create(ExtendedProperties properties, int singleStages, int doubleStages, Crop crop)
    {
        final IntegerProperty property = TFCBlockStateProperties.getAgeProperty(singleStages + doubleStages - 1);
        return new DoubleCropBlock(properties, singleStages - 1, singleStages + doubleStages - 1, TFCBlocks.DEAD_CROPS.get(crop), TFCItems.CROP_SEEDS.get(crop), crop.getPrimaryNutrient(), ClimateRanges.CROPS.get(crop))
        {
            @Override
            public IntegerProperty getAgeProperty()
            {
                return property;
            }
        };
    }

    protected final int maxSingleAge;
    protected final float maxSingleGrowth;

    protected DoubleCropBlock(ExtendedProperties properties, int maxSingleAge, int maxAge, Supplier<? extends Block> dead, Supplier<? extends Item> seeds, FarmlandBlockEntity.NutrientType primaryNutrient, Supplier<ClimateRange> climateRange)
    {
        super(properties, maxAge, dead, seeds, primaryNutrient, climateRange);

        this.maxSingleAge = maxSingleAge;
        this.maxSingleGrowth = (float) maxSingleAge / maxAge;
        registerDefaultState(defaultBlockState().setValue(PART, Part.BOTTOM));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final Part part = state.getValue(PART);
        final BlockState belowState = level.getBlockState(pos.below());
        if (part == Part.BOTTOM)
        {
            if (state.getValue(WILD))
            {
                return TFCTags.Blocks.WILD_CROP_GROWS_ON.contains(belowState.getBlock());
            }
            return TFCTags.Blocks.FARMLAND.contains(belowState.getBlock());
        }
        else
        {
            return belowState.is(this) && belowState.getValue(PART) == Part.BOTTOM;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(PART));
    }

    @Override
    public void growthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        // Only perform growth ticks on the lower part
        if (state.getValue(DoubleCropBlock.PART) == Part.BOTTOM)
        {
            super.growthTick(level, pos, state, crop);
        }
    }

    @Override
    protected void postGrowthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        final BlockPos posAbove = pos.above();
        final BlockState stateAbove = level.getBlockState(posAbove);
        final int age = Mth.clamp((int) (crop.getGrowth() * getMaxAge()), 0, getMaxAge());

        level.setBlock(pos, state.setValue(getAgeProperty(), age), Block.UPDATE_CLIENTS);
        if (age > maxSingleAge && (stateAbove.isAir() || stateAbove.getBlock() == this))
        {
            level.setBlock(posAbove, state.setValue(getAgeProperty(), age).setValue(PART, Part.TOP), Block.UPDATE_ALL);
        }
    }

    @Override
    public void die(Level level, BlockPos pos, BlockState state, boolean fullyGrown)
    {
        final BlockPos posAbove = pos.above();
        final BlockState stateAbove = level.getBlockState(posAbove);
        final BlockState deadState = dead.get().defaultBlockState().setValue(DeadCropBlock.MATURE, fullyGrown);
        if (fullyGrown && (stateAbove.getBlock() == this || stateAbove.isAir()))
        {
            level.setBlock(posAbove, deadState.setValue(DoubleDeadCropBlock.PART, Part.TOP), Block.UPDATE_CLIENTS);
        }
        else if (stateAbove.getBlock() == this)
        {
            level.destroyBlock(posAbove, false);
        }
        level.setBlockAndUpdate(pos, deadState.setValue(DoubleDeadCropBlock.PART, Part.BOTTOM));
    }

    @Override
    public float getGrowthLimit(Level level, BlockPos pos, BlockState state)
    {
        final BlockState stateAbove = level.getBlockState(pos.above());
        return stateAbove.isAir() || stateAbove.getBlock() == this ? CropHelpers.GROWTH_LIMIT : maxSingleGrowth;
    }

    public enum Part implements StringRepresentable
    {
        BOTTOM, TOP;

        private final String serializedName;

        Part()
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}