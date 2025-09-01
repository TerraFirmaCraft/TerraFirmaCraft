/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blocks.TFCBlocks;
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
            final ChunkData data = ChunkData.get(level, pos);
            final int stormHydration = (int) data.getStormHydration();
            final int totalRainHydration = FarmlandBlock.getRainHydration(level, pos, stormHydration);
            final int hydrationValue = FarmlandBlock.getHydrationFromRainHydration(level, pos, totalRainHydration);
            final int minRainfallHydration = (int) data.getMinRainfallHydration(pos);
            final int minHydrationValue = FarmlandBlock.getHydrationFromRainHydration(level, pos, minRainfallHydration);
            final int maxRainfallHydration = (int) data.getMaxRainfallHydration(pos);
            final int maxHydrationValue = FarmlandBlock.getHydrationFromRainHydration(level, pos, maxRainfallHydration);
            final MutableComponent tooltip = Component.translatable("tfc.tooltip.farmland.hydration", hydrationValue, minHydrationValue, maxHydrationValue);

            tooltip.append(switch (validRange.checkHydration(hydrationValue, allowWiggle))
            {
                case VALID -> Component.translatable("tfc.tooltip.farmland.just_right");
                case LOW -> Component.translatable("tfc.tooltip.farmland.hydration_too_low", validRange.getMinHydration(allowWiggle));
                case HIGH -> Component.translatable("tfc.tooltip.farmland.hydration_too_high", validRange.getMaxHydration(allowWiggle));
            });
            return tooltip;
        }

        return getHydrationTooltip(level, pos, validRange, allowWiggle, getHydrationFromStormHydration(level, pos, 0));
    }

    public static Component getHydrationTooltip(LevelAccessor level, BlockPos pos, ClimateRange validRange, boolean allowWiggle, int hydration)
    {
        final MutableComponent tooltip = Component.translatable("tfc.tooltip.farmland.hydration_simple", hydration);

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

    /**
     * @return A value in the range [0, 60] representing total rain hydration (humidity + storm)
     */
    public static int getRainHydration(Level level, BlockPos pos, int stormHydration)
    {
        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();

        final int humidityBoost = (int) (ChunkData.MAX_HUMIDITY_CONTRIBUTION * Mth.clampedMap(model.getRainfall(level, pos), ClimateModel.MIN_RAINFALL, ClimateModel.MAX_RAINFALL, 0, 1));
        // Ensure that storms + humidity do not exceed ChunkData.MAX_RAINFALL_CONTRIBUTION
        return (int) Mth.clamp(stormHydration + humidityBoost, 0, ChunkData.MAX_RAINFALL_CONTRIBUTION);
    }

    /**
     * @return A value in the range [0, 100]
     */
    public static int getHydrationFromStormHydration(Level level, BlockPos pos, int stormBoost)
    {
        if (Helpers.isFluid(level.getFluidState(pos.above()), TFCTags.Fluids.HYDRATING))
        {
            return 100; // special case for waterlogged crops
        }

        final int rainBoost = getRainHydration(level, pos, stormBoost);
        final int waterBoost = isSourceBlockPresent(level, pos) ? 40 : 0;

        return Mth.clamp(waterBoost + rainBoost, 0, 100);
    }

    /**
     * @return A value in the range [0, 100]
     * Mirrors getHydrationFromStormHydration
     */
    public static int getHydrationFromRainHydration(Level level, BlockPos pos, int rainBoost)
    {
        if (Helpers.isFluid(level.getFluidState(pos.above()), TFCTags.Fluids.HYDRATING))
        {
            return 100; // special case for waterlogged crops
        }

        final int waterBoost = isSourceBlockPresent(level, pos) ? 40 : 0;
        final float soilMultiplier = getHydrationMultiplier(level, pos);

        return Mth.clamp((int) ((waterBoost + rainBoost) * soilMultiplier), 0, 100);
    }

    /**
     * @return A value in the range [0, 100]
     */
    public static int getHydrationFromStormHydrationOverTime(Level level, BlockPos pos, int stormBoost, long fromTick, long toTick)
    {
        if (Helpers.isFluid(level.getFluidState(pos.above()), TFCTags.Fluids.HYDRATING))
        {
            return 100; // special case for waterlogged crops
        }

        final int rainBoost = getRainHydrationOverTime(level, pos, stormBoost, fromTick, toTick);
        final int waterBoost = isSourceBlockPresent(level, pos) ? 40 : 0;
        final float soilMultiplier = getHydrationMultiplier(level, pos);

        return Mth.clamp((int) ((waterBoost + rainBoost) * soilMultiplier), 0, 100);
    }

    /**
     * @return A value in the range [0, 60] representing total rain hydration (humidity + storm)
     */
    public static int getRainHydrationOverTime(Level level, BlockPos pos, int stormHydration, long fromTick, long toTick)
    {

        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();
        final ICalendar calendar = Calendars.get(level);

        final int humidityBoost = (int) (ChunkData.MAX_HUMIDITY_CONTRIBUTION * Mth.clampedMap(model.getRainfall(level, pos, fromTick, toTick, calendar.getCalendarDaysInMonth()), ClimateModel.MIN_RAINFALL, ClimateModel.MAX_RAINFALL, 0, 1));
        // Ensure that storms + humidity do not exceed ChunkData.MAX_RAINFALL_CONTRIBUTION
        return (int) Mth.clamp(stormHydration + humidityBoost, 0, ChunkData.MAX_RAINFALL_CONTRIBUTION);
    }

    public static void turnToDirt(BlockState state, Level level, BlockPos pos)
    {
        level.setBlockAndUpdate(pos, pushEntitiesUp(state, ((FarmlandBlock) state.getBlock()).getDirt(), level, pos));
    }

    /**
     * @return Value in [0.5, 2]
     */
    public static float getHydrationMultiplier(Level level, BlockPos pos)
    {
        final BlockState block = level.getBlockState(pos.below());
        if (block.is(TFCTags.Blocks.INCREASES_SOIL_HYDRATION))
        {
            return 2f;
        }
        if (block.is(TFCTags.Blocks.DECREASES_SOIL_HYDRATION))
        {
            return 0.5f;
        }
        return 1f;
    }

    /**
     * @return True if there is a water block in range
     */
    public static boolean isSourceBlockPresent(Level level, BlockPos pos)
    {
        return findMinCostWater(level, pos) < 5;
    }

    /**
     * @return A value in [1, 5]
     * TODO: Probably berry bushes should not use this system anymore.
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
