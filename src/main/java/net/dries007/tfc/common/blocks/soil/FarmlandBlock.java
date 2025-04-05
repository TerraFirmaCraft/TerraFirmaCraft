/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.crop.CropHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.registry.RegistrySoilVariant;

public class FarmlandBlock extends Block implements ISoilBlock, HoeOverlayBlock, IForgeBlockExtension, EntityBlockExtension
{
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    public static Component getHydrationTooltip(Level level, BlockPos pos, ClimateRange validRange, boolean allowWiggle)
    {
        float accumulatedRainfall = 0;
        if (level.getBlockEntity(pos) instanceof IFarmland farmland)
        {
            accumulatedRainfall = farmland.getAdditionalWater();
        }

        return getHydrationTooltip(level, pos, validRange, allowWiggle, getHydration(level, pos, accumulatedRainfall));
    }

    public static Component getHydrationTooltip(LevelAccessor level, BlockPos pos, ClimateRange validRange, boolean allowWiggle, int hydration)
    {
        final MutableComponent tooltip = Component.translatable("tfc.tooltip.farmland.hydration", hydration);

        tooltip.append(switch (validRange.checkHydration(hydration, allowWiggle))
            {
                case VALID -> Component.translatable("tfc.tooltip.farmland.just_right");
                case LOW -> Component.translatable("tfc.tooltip.farmland.hydration_too_low", validRange.getMinHydration(allowWiggle));
                case HIGH -> Component.translatable("tfc.tooltip.farmland.hydration_too_high", validRange.getMaxHydration(allowWiggle));
            });
        return tooltip;
    }

    public static Component getTemperatureTooltip(Level level, BlockPos pos, ClimateRange validRange, boolean allowWiggle)
    {
        return getTemperatureTooltip(level, pos, validRange, Climate.getTemperature(level, pos), allowWiggle, "tfc.tooltip.farmland.temperature");
    }

    public static Component getAverageTemperatureTooltip(Level level, BlockPos pos, ClimateRange validRange, boolean allowWiggle)
    {
        return getTemperatureTooltip(level, pos, validRange, Climate.getAverageTemperature(level, pos), allowWiggle, "tfc.tooltip.climate_average_temperature");
    }

    public static Component getTemperatureTooltip(Level level, BlockPos pos, ClimateRange validRange, float temperature, boolean allowWiggle, String translationKey)
    {
        final MutableComponent tooltip = Component.translatable(translationKey, String.format("%.1f", temperature));

        tooltip.append(switch (validRange.checkTemperature(temperature, allowWiggle))
            {
                case VALID -> Component.translatable("tfc.tooltip.farmland.just_right");
                case LOW -> Component.translatable("tfc.tooltip.farmland.temperature_too_low", validRange.getMinTemperature(allowWiggle));
                case HIGH -> Component.translatable("tfc.tooltip.farmland.temperature_too_high", validRange.getMaxTemperature(allowWiggle));
            });
        return tooltip;
    }

    public static int getRainfallBoost(float accumulatedRainfall)
    {
        return (int) (30 * accumulatedRainfall / ChunkData.MAX_ACCUMULATED_RAINFALL);
    }

    /**
     * @return A value in the range [0, 100]
     */
    public static int getHydration(Level level, BlockPos pos, float accumulatedRainfall)
    {
        if (Helpers.isFluid(level.getFluidState(pos.above()), TFCTags.Fluids.HYDRATING))
        {
            return 100; // special case for waterlogged crops
        }

        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();

        final int waterCost = findMinCostWater(level, pos); // Nearby water contributes an additional 0 - 80% hydration based on proximity
        final int waterBoost = 20 * (5 - waterCost); // Nearby water contributes an additional 0 - 80% hydration based on proximity
        final int rainfallBoost = getRainfallBoost(accumulatedRainfall); // Up to 30% bonus from rainfall
        // TODO :: Maybe this humidity factor should have a lower impact when temperature is lower?
        final int humidityBoost = (int) (30 * Mth.clampedMap(model.getGroundwater(level, pos), ClimateModel.MIN_RAINFALL, ClimateModel.MAX_RAINFALL, 0, 1)); // Up to 30% bonus from groundwater
        return Mth.clamp(rainfallBoost + waterBoost + humidityBoost, 0, 100);
    }

    /**
     * @return A value in the range [0, 100]
     */
    public static int getHydration(Level level, BlockPos pos, float accumulatedRainfall, long fromTick, long toTick)
    {
        if (Helpers.isFluid(level.getFluidState(pos.above()), TFCTags.Fluids.HYDRATING))
        {
            return 100; // special case for waterlogged crops
        }

        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();
        final ICalendar calendar = Calendars.get(level);

        final int waterCost = findMinCostWater(level, pos); // Nearby water contributes an additional 0 - 80% hydration based on proximity
        final int waterBoost = 20 * (5 - waterCost); // Nearby water contributes an additional 0 - 80% hydration based on proximity
        final int rainfallBoost = getRainfallBoost(accumulatedRainfall); // Up to 30% bonus from rainfall
        // TODO :: Maybe this humidity factor should have a lower impact when temperature is lower?
        final int humidityBoost = (int) (30 * Mth.clampedMap(model.getGroundwater(level, pos, fromTick, toTick, calendar.getCalendarDaysInMonth()), ClimateModel.MIN_RAINFALL, ClimateModel.MAX_RAINFALL, 0, 1)); // Up to 30% bonus from groundwater
        return Mth.clamp(rainfallBoost + waterBoost + humidityBoost, 0, 100);
    }

    public static void turnToDirt(BlockState state, Level level, BlockPos pos)
    {
        level.setBlockAndUpdate(pos, pushEntitiesUp(state, ((FarmlandBlock) state.getBlock()).getDirt(), level, pos));
    }

    /**
     * @return A value in [1, 5]
     */
    public static int findMinCostWater(LevelAccessor level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        int minCostWater = 5;
        for (int dx = -4; dx <= 4; dx++)
        {
            for (int dz = -4; dz <= 4; dz++)
            {
                for (int dy = -1; dy <= 0; dy++)
                {
                    final int cost = Math.max(Math.abs(dx), Math.abs(dz)) + (-2 * dy);
                    if (cost < minCostWater && Helpers.isFluid(level.getFluidState(cursor.setWithOffset(pos, dx, dy, dz)).getType(), TFCTags.Fluids.HYDRATING))
                    {
                        minCostWater = cost;
                        if (minCostWater == 1)
                        {
                            return 1;
                        }
                    }
                }
            }
        }
        return minCostWater;
    }

    private final ExtendedProperties properties;
    private final Supplier<? extends Block> dirt;

    public FarmlandBlock(ExtendedProperties properties, Supplier<? extends Block> dirt)
    {
        super(properties.properties());

        this.properties = properties;
        this.dirt = dirt;
    }

    FarmlandBlock(ExtendedProperties properties, RegistrySoilVariant variant)
    {
        this(properties, variant.getBlock(SoilBlockType.DIRT));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        return CropHelpers.useFertilizer(level, player, hand, pos)
            ? ItemInteractionResult.sidedSuccess(level.isClientSide)
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState defaultState = defaultBlockState();
        return defaultState.canSurvive(context.getLevel(), context.getClickedPos()) ? defaultState : getDirt();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.UP && !state.canSurvive(level, currentPos))
        {
            level.scheduleTick(currentPos, this, 1);
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation") // isSolid()
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockState stateAbove = level.getBlockState(pos.above());
        return !stateAbove.isSolid() || stateAbove.getBlock() instanceof FenceGateBlock || stateAbove.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        tick(state, level, pos, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!state.canSurvive(level, pos))
        {
            turnToDirt(state, level, pos);
        }
        else
        {
            // Only perform rainfall calculation on server.
            if (!level.isClientSide())
            {
                if (level.getBlockEntity(pos) instanceof IFarmland farmland)
                {
                    farmland.waterTick();
                }
            }
        }
    }

    @Override
    public BlockState getDirt()
    {
        return dirt.get().defaultBlockState();
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean isDebug)
    {
        level.getBlockEntity(pos, TFCBlockEntities.FARMLAND.get()).ifPresent(farmland -> farmland.addHoeOverlayInfo(level, pos, text, true, true));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
