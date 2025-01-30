/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.ClimateRange;

public abstract class CropBlock extends net.minecraft.world.level.block.CropBlock implements HoeOverlayBlock, ICropBlock, IForgeBlockExtension, EntityBlockExtension
{
    public static final VoxelShape QUARTER_SHAPE = box(2, 0, 2, 14, 4, 14);
    public static final VoxelShape HALF_SHAPE = box(2, 0, 2, 14, 8, 14);
    public static final VoxelShape FULL_SHAPE = box(2, 0, 2, 14, 16, 14);

    protected final FarmlandBlockEntity.NutrientType primaryNutrient;
    protected final Supplier<? extends Block> dead;
    protected final Supplier<? extends Item> seeds;
    protected final Supplier<ClimateRange> climateRange;
    protected final int maxAge;

    private final ExtendedProperties extendedProperties;

    protected CropBlock(ExtendedProperties properties, int maxAge, Supplier<? extends Block> dead, Supplier<? extends Item> seeds, FarmlandBlockEntity.NutrientType primaryNutrient, Supplier<ClimateRange> climateRange)
    {
        super(properties.properties());

        this.extendedProperties = properties;
        this.maxAge = maxAge;

        this.dead = dead;
        this.seeds = seeds;
        this.primaryNutrient = primaryNutrient;
        this.climateRange = climateRange;

        registerDefaultState(defaultBlockState().setValue(getAgeProperty(), 0));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return extendedProperties;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        float growth = (float) state.getValue(getAgeProperty()) / getMaxAge();
        if (growth <= 0.25F) return QUARTER_SHAPE;
        else if (growth <= 0.5F) return HALF_SHAPE;
        return FULL_SHAPE;
    }

    @Override
    public abstract IntegerProperty getAgeProperty();

    @Override
    public int getMaxAge()
    {
        return maxAge;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        tick(state, level, pos, random);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.FARMLANDS);
    }

    @Override
    protected ItemLike getBaseSeedId()
    {
        return seeds.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(getAgeProperty());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        return CropHelpers.useFertilizer(level, player, hand, pos.below())
            ? ItemInteractionResult.sidedSuccess(level.isClientSide)
            : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!level.isClientSide())
        {
            if (canSurvive(state, level, pos))
            {
                if (level.getBlockEntity(pos) instanceof CropBlockEntity crop)
                {
                    growthTick(level, pos, state, crop);
                }
            }
            else
            {
                // Cannot survive here (e.g. no farmland below)
                level.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean isDebug)
    {
        final ClimateRange range = climateRange.get();
        final BlockPos sourcePos = pos.below();

        text.accept(FarmlandBlock.getTemperatureTooltip(level, pos, range, false));
        text.accept(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));

        IFarmland farmland = null;
        if (level.getBlockEntity(sourcePos) instanceof IFarmland found)
        {
            farmland = found;
        }
        else if (level.getBlockEntity(sourcePos.below()) instanceof IFarmland found)
        {
            farmland = found;
        }
        if (farmland != null)
        {
            farmland.addTooltipInfo(text);
        }

        if (level.getBlockEntity(pos) instanceof CropBlockEntity crop)
        {
            if (isDebug)
            {
                text.accept(Component.literal(String.format("[Debug] Growth = %.4f Yield = %.4f Expiry = %.4f Last Tick = %d Delta = %d", crop.getGrowth(), crop.getYield(), crop.getExpiry(), crop.getLastGrowthTick(), Calendars.get(level).getTicks() - crop.getLastGrowthTick())));
            }
            if (crop.getGrowth() >= 1)
            {
                text.accept(Component.translatable("tfc.tooltip.farmland.mature"));
            }
        }
    }

    @Override
    public void growthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        // Only perform growth ticks on server.
        if (!level.isClientSide() && CropHelpers.growthTick(level, pos, state, crop))
        {
            postGrowthTick(level, pos, state, crop);
        }
    }

    @Override
    public ClimateRange getClimateRange()
    {
        return climateRange.get();
    }

    @Override
    public FarmlandBlockEntity.NutrientType getPrimaryNutrient()
    {
        return primaryNutrient;
    }

    protected abstract void postGrowthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop);
}