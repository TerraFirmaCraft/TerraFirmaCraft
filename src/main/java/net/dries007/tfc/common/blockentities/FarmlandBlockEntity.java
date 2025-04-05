/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.data.Fertilizer;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType.*;

public class FarmlandBlockEntity extends TFCBlockEntity implements IFarmland, ICalendarTickable
{
    private static final float MAX_ADDITIONAL_WATER = 25.0f;

    private long lastUpdateTick; // The last tick this farmland was ticked via the block entity's tick() method. A delta of > 1 is used to detect time skips
    private long lastWaterTick; // The last tick the farmland block was ticked via waterTick()

    // TODO :: Actually connect additionalWater to some gameplay system (e.g. watering the soil)
    private float nitrogen, phosphorous, potassium, additionalWater;

    public FarmlandBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.FARMLAND.get(), pos, state);
    }

    protected FarmlandBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        lastUpdateTick = Integer.MIN_VALUE;
        lastWaterTick = Calendars.SERVER.getTicks();
        nitrogen = phosphorous = potassium = additionalWater = 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FarmlandBlockEntity farmland)
    {
        farmland.checkForCalendarUpdate();
    }

    public void waterTick()
    {
        assert level != null;
        final ICalendar calendar = Calendars.get(level);
        final long firstCalendarTick = calendar.getCalendarTicks() + calendar.getFixedCalendarTicksFromTick(this.getLastWaterTick() - calendar.getTicks());
        final long secondCalendarTick = calendar.getCalendarTicks() + calendar.getFixedCalendarTicksFromTick(Calendars.SERVER.getTicks() - calendar.getTicks());
        updateAdditionalWater(firstCalendarTick, secondCalendarTick);
        setLastWaterTick(Calendars.SERVER.getTicks());
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        BlockEntity entity = level.getBlockEntity(worldPosition);
        if (entity instanceof IFarmland)
        {
            this.waterTick();
        }
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        loadNutrients(nbt);
        loadAdditionalWater(nbt);
        lastUpdateTick = nbt.getLong("tick");
        lastWaterTick = nbt.getLong("waterTick");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        saveNutrients(nbt);
        saveAdditionalWater(nbt);
        nbt.putLong("tick", lastUpdateTick);
        nbt.putLong("waterTick", lastWaterTick);
        super.saveAdditional(nbt, provider);
    }

    public void addHoeOverlayInfo(Level level, BlockPos pos, Consumer<Component> text, boolean includeHydration, boolean includeNutrients)
    {
        if (includeHydration)
        {
            float accumulatedRainfallValue = ChunkData.get(level, pos).getAccumulatedRainfall();
            final int hydrationValue = FarmlandBlock.getHydration(level, pos, accumulatedRainfallValue);
            final MutableComponent hydration = Component.translatable("tfc.tooltip.farmland.hydration", hydrationValue);
            final MutableComponent accumulatedRainfall = Component.translatable("tfc.tooltip.farmland.accumulated_rainfall", (int) accumulatedRainfallValue, FarmlandBlock.getRainfallBoost(accumulatedRainfallValue));
            text.accept(hydration);
            text.accept(accumulatedRainfall);
        }

        if (includeNutrients)
        {
            addTooltipInfo(text);
        }
    }

    @Override
    public float getNutrient(NutrientType type)
    {
        return switch (type)
        {
            case NITROGEN -> nitrogen;
            case PHOSPHOROUS -> phosphorous;
            case POTASSIUM -> potassium;
        };
    }

    @Override
    public void setNutrient(NutrientType type, float value)
    {
        setNutrientWithoutSync(type, value);
        markForSync();
    }

    @Override
    public void addNutrients(Fertilizer fertilizer, float multiplier)
    {
        // Override to not send three sync packets
        setNutrientWithoutSync(NITROGEN, getNutrient(NITROGEN) + (fertilizer.nitrogen() * multiplier));
        setNutrientWithoutSync(PHOSPHOROUS, getNutrient(PHOSPHOROUS) + (fertilizer.phosphorus() * multiplier));
        setNutrientWithoutSync(POTASSIUM, getNutrient(POTASSIUM) + (fertilizer.potassium() * multiplier));
        markForSync();
    }

    @Override
    public void setNutrientWithoutSync(NutrientType type, float value)
    {
        value = Mth.clamp(value, 0, 1);
        switch (type)
        {
            case NITROGEN -> nitrogen = value;
            case PHOSPHOROUS -> phosphorous = value;
            case POTASSIUM -> potassium = value;
        }
    }

    @Override
    public float getAdditionalWater()
    {
        return additionalWater;
    }

    @Override
    public void setAdditionalWater(float additionalWater)
    {
        this.additionalWater = Mth.clamp(additionalWater, 0, MAX_ADDITIONAL_WATER);
        markForSync();
    }

    public long getLastWaterTick()
    {
        return lastWaterTick;
    }

    public void setLastWaterTick(long lastWaterTick)
    {
        this.lastWaterTick = lastWaterTick;
        markForSync();
    }

    public enum NutrientType
    {
        NITROGEN, PHOSPHOROUS, POTASSIUM;

        public static final NutrientType[] VALUES = values();
    }
}
