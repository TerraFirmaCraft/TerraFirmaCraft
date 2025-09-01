/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.data.Fertilizer;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Common growth logic for crop blocks
 * <a href="https://www.desmos.com/calculator/wew3pvmijq">Reference</a>
 */
public final class CropHelpers
{
    public static final long UPDATE_INTERVAL = 2 * ICalendar.CALENDAR_TICKS_IN_DAY;

    public static final float GROWTH_FACTOR = 1f / (24 * ICalendar.CALENDAR_TICKS_IN_DAY);
    public static final float NUTRIENT_CONSUMPTION = 1f / (12 * ICalendar.CALENDAR_TICKS_IN_DAY);
    public static final float NUTRIENT_GROWTH_FACTOR = 0.5f;
    public static final float GROWTH_LIMIT = 1f;
    public static final float EXPIRY_LIMIT = 2f;
    public static final float YIELD_MIN = 0.2f;
    public static final float YIELD_LIMIT = 1f;

    public static boolean lightValid(Level level, BlockPos pos)
    {
        return level.getRawBrightness(pos, 0) >= 12;
    }

    /**
     * @return {@code true} if the crop survived.
     */
    public static boolean growthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        final long firstTick = crop.getLastGrowthTick(), thisTick = Calendars.SERVER.getTicks();
        long tick = firstTick + CropHelpers.UPDATE_INTERVAL, lastTick = firstTick;
        for (; tick < thisTick; tick += CropHelpers.UPDATE_INTERVAL)
        {
            if (!CropHelpers.growthTickStep(level, pos, state, level.getRandom(), lastTick, tick, crop))
            {
                return false;
            }
            lastTick = tick;
        }
        return lastTick >= thisTick || CropHelpers.growthTickStep(level, pos, state, level.getRandom(), lastTick, thisTick, crop);
    }

    public static boolean growthTickStep(Level level, BlockPos pos, BlockState state, RandomSource random, long fromTick, long toTick, CropBlockEntity crop)
    {
        // Calculate invariants
        final ICalendar calendar = Calendars.get(level);
        final BlockPos sourcePos = pos.below();
        final long firstCalendarTick = calendar.getCalendarTicks() + calendar.getFixedCalendarTicksFromTick(fromTick - calendar.getTicks());
        final long secondCalendarTick = calendar.getCalendarTicks() + calendar.getFixedCalendarTicksFromTick(toTick - calendar.getTicks());
        final float startTemperature = Climate.getTemperature(level, pos, calendar, firstCalendarTick);
        final float endTemperature = Climate.getTemperature(level, pos, calendar, secondCalendarTick);
        final long tickDelta = toTick - fromTick;

        final ICropBlock cropBlock = (ICropBlock) state.getBlock();
        final ClimateRange range = cropBlock.getClimateRange();

        // This is based on an average hydration over the skipped period, and the current storm hydration
        final int hydration = FarmlandBlock.getHydrationFromStormHydrationOverTime(level, sourcePos, (int) ChunkData.get(level, pos).getStormHydration(), fromTick, toTick);
        final boolean growing = checkClimate(range, hydration, startTemperature, endTemperature, false);
        final boolean healthy = growing || checkClimate(range, hydration, startTemperature, endTemperature, true);

        // Nutrients are consumed first, since they are independent of growth or health.
        // As long as the crop exists it consumes nutrients.

        // Nutrients required for 100% yield multiplier
        // Negative values are nutrients restored to soil
        final float nForGrowth = cropBlock.getNForGrowth();
        final float pForGrowth = cropBlock.getPForGrowth();
        final float kForGrowth = cropBlock.getKForGrowth();

        final float posNForGrowth = Math.max(0, nForGrowth);
        final float posPForGrowth = Math.max(0, pForGrowth);
        final float posKForGrowth = Math.max(0, kForGrowth);

        float nutrientsForGrowth = posNForGrowth + posPForGrowth + posKForGrowth;

        // Required nutrients for this growth tick
        final float nRequired = NUTRIENT_CONSUMPTION * tickDelta * nForGrowth;
        final float pRequired = NUTRIENT_CONSUMPTION * tickDelta * pForGrowth;
        final float kRequired = NUTRIENT_CONSUMPTION * tickDelta * kForGrowth;

        final float nutrientsRequired = Math.max(0, nRequired) + Math.max(0, pRequired) + Math.max(0, kRequired);

        // Consumed nutrients for this growth tick
        float nutrientsConsumed = 0;
        float nutrientsAvailable = 0;

        // How many nutrients were absorbed relative to the crop's capacity
        if (level.getBlockEntity(sourcePos) instanceof IFarmland farmland && nutrientsForGrowth > 0)
        {
            // Sum of all nutrients available for growth
            nutrientsAvailable = (
                Math.min(posNForGrowth, farmland.getNutrient(FarmlandBlockEntity.NutrientType.NITROGEN))
                    + Math.min(posPForGrowth, farmland.getNutrient(FarmlandBlockEntity.NutrientType.PHOSPHOROUS))
                    + Math.min(posKForGrowth, farmland.getNutrient(FarmlandBlockEntity.NutrientType.POTASSIUM))
            );

            // Won't consume a nutrient beyond the amount required by the crop
            final float maxNToConsume = nForGrowth - crop.getNAbsorbed();
            final float maxPToConsume = pForGrowth - crop.getPAbsorbed();
            final float maxKToConsume = kForGrowth - crop.getKAbsorbed();

            final float nConsumed = farmland.consumeNutrients(Math.min(nRequired, maxNToConsume), FarmlandBlockEntity.NutrientType.NITROGEN);
            final float pConsumed = farmland.consumeNutrients(Math.min(pRequired, maxPToConsume), FarmlandBlockEntity.NutrientType.PHOSPHOROUS);
            final float kConsumed = farmland.consumeNutrients(Math.min(kRequired, maxKToConsume), FarmlandBlockEntity.NutrientType.POTASSIUM);

            // Adds new nutrients back to the crop
            crop.addNutrients(nConsumed, pConsumed, kConsumed);

            nutrientsConsumed += nConsumed + pConsumed + kConsumed;
        }
        else
        {
            // Avoids division by zero
            nutrientsForGrowth = 1f;
        }

        final float growthModifier = TFCConfig.SERVER.cropGrowthModifier.get().floatValue(); // Higher = Slower growth
        final float expiryModifier = TFCConfig.SERVER.cropExpiryModifier.get().floatValue(); // Higher = Slower expiry
        final float localExpiryLimit = EXPIRY_LIMIT * expiryModifier * (1f / growthModifier);

        // Total growth is based on the ticks and the nutrients consumed. It is then allocated to actual growth or expiry based on other factors.
        final float totalGrowthDelta = (1f / growthModifier) * Helpers.uniform(random, 0.9f, 1.1f) * tickDelta * CropHelpers.GROWTH_FACTOR + nutrientsConsumed / nutrientsForGrowth * NUTRIENT_GROWTH_FACTOR;
        final float initialGrowth = crop.getGrowth();
        float remainingGrowthDelta = totalGrowthDelta;
        float growth = initialGrowth, expiry = crop.getExpiry(), actualYield = crop.getYield();

        // Re-scale expiry to within our imaginary limits
        expiry *= localExpiryLimit / EXPIRY_LIMIT;

        final float growthLimit = cropBlock.getGrowthLimit(level, pos, state);
        if (remainingGrowthDelta > 0 && growing && growth < growthLimit)
        {
            // Allocate to growth
            final float delta = Math.min(remainingGrowthDelta, growthLimit - growth);

            growth += delta;
            remainingGrowthDelta -= delta;
        }
        if (remainingGrowthDelta > 0)
        {
            // Allocate remaining growth to expiry
            final float delta = Math.min(remainingGrowthDelta, localExpiryLimit - expiry);

            expiry += delta;
        }

        // Add nutrients back to soil. Must happen after we determine the growth delta to prevent extra nutrients being added
        final float growthDelta = growth - initialGrowth;
        if (level.getBlockEntity(sourcePos) instanceof IFarmland farmland)
        {
            final float percentOfNutrientsSatisfied = nutrientsRequired > 0 ? nutrientsConsumed / nutrientsRequired : 0f;
            final float bonus = getSoilModifier(level.getBlockState(sourcePos));

            farmland.produceNutrients(nForGrowth * bonus, FarmlandBlockEntity.NutrientType.NITROGEN, percentOfNutrientsSatisfied, growthDelta);
            farmland.produceNutrients(pForGrowth * bonus, FarmlandBlockEntity.NutrientType.PHOSPHOROUS, percentOfNutrientsSatisfied, growthDelta);
            farmland.produceNutrients(kForGrowth * bonus, FarmlandBlockEntity.NutrientType.POTASSIUM, percentOfNutrientsSatisfied, growthDelta);
        }

        // Calculate yield, which depends on the nutrient satisfaction, which is a measure of nutrient consumption over the growth time.
        final float nutrientSatisfaction;

        if (growthDelta <= 0)
        {
            nutrientSatisfaction = 1; // Either condition causes the below formula to result in NaN
        }
        else if (nutrientsRequired <= 0)
        {
            nutrientSatisfaction = 0; // No yield bonuses for plants that don't absorb nutrients
        }
        else
        {
            nutrientSatisfaction = Math.min(1, (totalGrowthDelta / growthDelta) * (nutrientsAvailable / nutrientsRequired));
        }

        actualYield += growthDelta * Helpers.lerp(nutrientSatisfaction, YIELD_MIN, YIELD_LIMIT);

        // Check if the crop should've expired.
        if (expiry >= localExpiryLimit || !healthy)
        {
            // Lenient here - instead of assuming it expired at the start of the duration, we assume at the end. Including growth during this period.
            cropBlock.die(level, pos, state, growth >= 1);
            return false;
        }

        // Re-scale expiry to constant values to maintain invariance if the config value is updated
        expiry *= EXPIRY_LIMIT / localExpiryLimit;

        crop.setGrowth(growth);
        crop.setYield(actualYield);
        crop.setExpiry(expiry);
        crop.setLastGrowthTick(calendar.getTicks());

        return true;
    }

    private static boolean checkClimate(ClimateRange range, int hydration, float firstTemperature, float secondTemperature, boolean allowWiggle)
    {
        return range.checkBoth(hydration, firstTemperature, allowWiggle) && range.checkTemperature(secondTemperature, allowWiggle) == ClimateRange.Result.VALID;
    }

    public static boolean useFertilizer(Level level, Player player, InteractionHand hand, BlockPos farmlandPos)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final Fertilizer fertilizer = Fertilizer.get(stack);
        if (fertilizer != null && level.getBlockEntity(farmlandPos) instanceof IFarmland farmland)
        {
            if (!level.isClientSide())
            {
                final float bonus = getSoilModifier(level.getBlockState(farmlandPos));
                int repeat = -1;
                if (player.isShiftKeyDown())
                {
                    repeat = minAmountRequiredToNextFillBar(farmland, fertilizer, FarmlandBlockEntity.NutrientType.NITROGEN, repeat, bonus);
                    repeat = minAmountRequiredToNextFillBar(farmland, fertilizer, FarmlandBlockEntity.NutrientType.POTASSIUM, repeat, bonus);
                    repeat = minAmountRequiredToNextFillBar(farmland, fertilizer, FarmlandBlockEntity.NutrientType.PHOSPHOROUS, repeat, bonus);
                    repeat = Math.min(repeat, stack.getCount());
                }
                if (repeat == -1)
                {
                    repeat = 1; // By default, we consume 1
                }
                if ((fertilizer.nitrogen() == 0 || farmland.getNutrient(FarmlandBlockEntity.NutrientType.NITROGEN) == 1)
                    && (fertilizer.potassium() == 0 || farmland.getNutrient(FarmlandBlockEntity.NutrientType.POTASSIUM) == 1)
                    && (fertilizer.phosphorus() == 0 || farmland.getNutrient(FarmlandBlockEntity.NutrientType.PHOSPHOROUS) == 1))
                {
                    // Don't consume any fertilizer, as it won't do anything.
                    return false;
                }

                farmland.addNutrients(fertilizer, repeat * bonus);
                if (!player.isCreative())
                {
                    stack.shrink(repeat);
                }

                IFarmland.addNutrientParticles((ServerLevel) level, farmlandPos.above(), fertilizer);
                Helpers.playSound(level, farmlandPos, TFCSounds.FERTILIZER_USE.get());

                if (farmland.isMaxedOut() && player instanceof ServerPlayer serverPlayer)
                {
                    TFCAdvancements.FULL_FERTILIZER.trigger(serverPlayer);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Get fertilizer bonus based on soil type
     */
    public static float getSoilModifier(BlockState state)
    {
        if (Helpers.isBlock(state, TFCTags.Blocks.VERY_RICH_FARMLAND))
        {
            return 1.2f;
        }
        if (Helpers.isBlock(state, TFCTags.Blocks.RICH_FARMLAND))
        {
            return 1.1f;
        }
        if (Helpers.isBlock(state, TFCTags.Blocks.POOR_FARMLAND))
        {
            return 0.9f;
        }
        if (Helpers.isBlock(state, TFCTags.Blocks.VERY_POOR_FARMLAND))
        {
            return 0.8f;
        }
        return 1.0f;
    }

    private static int minAmountRequiredToNextFillBar(IFarmland farmland, Fertilizer fertilizer, FarmlandBlockEntity.NutrientType type, int prevValue)
    {
        return minAmountRequiredToNextFillBar(farmland, fertilizer, type, prevValue, 1f);
    }

    /**
     * We do this instead of looping because then we only call `addNutrients` once and reduce network load, since that will cause a sync.
     */
    private static int minAmountRequiredToNextFillBar(IFarmland farmland, Fertilizer fertilizer, FarmlandBlockEntity.NutrientType type, int prevValue, float bonus)
    {
        final float amount = fertilizer.getNutrient(type) * bonus;
        if (amount > 0 && farmland.getNutrient(type) < 1)
        {
            final int requiredValue = Mth.ceil((1 - farmland.getNutrient(type)) / amount);
            if (prevValue == -1 || requiredValue < prevValue)
            {
                return requiredValue;
            }
        }
        return prevValue;
    }
}
