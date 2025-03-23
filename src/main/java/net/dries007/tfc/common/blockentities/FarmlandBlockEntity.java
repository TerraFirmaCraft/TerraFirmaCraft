/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import java.util.function.Consumer;

import net.dries007.tfc.common.blocks.crop.CropHelpers;
import net.dries007.tfc.common.blocks.crop.ICropBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
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
import net.dries007.tfc.util.data.Fertilizer;
import org.spongepowered.asm.mixin.Mutable;

import static net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType.*;

public class FarmlandBlockEntity extends TFCBlockEntity implements IFarmland, ICalendarTickable
{
    // Rainfall is in MM
    public static float MAX_ACCUMULATED_RAINFALL = 100.0f;
    private static final long UPDATE_INTERVAL = 2 * ICalendar.CALENDAR_TICKS_IN_HOUR;

    private long lastUpdateTick; // The last tick this farmland was ticked via the block entity's tick() method. A delta of > 1 is used to detect time skips
    private long lastRainTick; // The last tick the farmland block was ticked via rainTick()

    private float nitrogen, phosphorous, potassium, accumulatedRainfall;

    public FarmlandBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.FARMLAND.get(), pos, state);
    }

    protected FarmlandBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        lastUpdateTick = Integer.MIN_VALUE;
        lastRainTick = Calendars.SERVER.getTicks();
        nitrogen = phosphorous = potassium = accumulatedRainfall = 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FarmlandBlockEntity farmland)
    {
        farmland.checkForCalendarUpdate();
    }

    public void rainTick()
    {
        assert level != null;
        final long firstTick = this.getLastRainTick(), thisTick = Calendars.SERVER.getTicks();
        long tick = firstTick + UPDATE_INTERVAL, lastTick = firstTick;
        for (; tick < thisTick; tick += UPDATE_INTERVAL)
        {
            final ICalendar calendar = Calendars.get(level);
            final long firstCalendarTick = calendar.getCalendarTicks() + calendar.getFixedCalendarTicksFromTick(lastTick - calendar.getTicks());
            final long secondCalendarTick = calendar.getCalendarTicks() + calendar.getFixedCalendarTicksFromTick(tick - calendar.getTicks());
            updateAccumulatedRainfall(level, worldPosition, firstCalendarTick, secondCalendarTick);
            lastTick = tick;
        }
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        BlockEntity entity = level.getBlockEntity(worldPosition);
        if (entity instanceof IFarmland)
        {
            rainTick();
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
        loadAccumulatedRainfall(nbt);
        lastUpdateTick = nbt.getLong("tick");
        lastRainTick = nbt.getLong("rainTick");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        saveNutrients(nbt);
        saveAccumulatedRainfall(nbt);
        nbt.putLong("tick", lastUpdateTick);
        nbt.putLong("rainTick", lastRainTick);
        super.saveAdditional(nbt, provider);
    }

    public void addHoeOverlayInfo(Level level, BlockPos pos, Consumer<Component> text, boolean includeHydration, boolean includeNutrients)
    {
        if (includeHydration)
        {
            final int value = FarmlandBlock.getHydration(level, pos, getAccumulatedRainfall());
            final MutableComponent hydration = Component.translatable("tfc.tooltip.farmland.hydration", value);
            final MutableComponent accumulatedRainfall = Component.translatable("tfc.tooltip.farmland.accumulated_rainfall", getAccumulatedRainfall());
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
    public float getAccumulatedRainfall() {
        return accumulatedRainfall;
    }

    @Override
    public void setAccumulatedRainfall(float accumulatedRainfall) {
        this.accumulatedRainfall = Mth.clamp(accumulatedRainfall, 0, MAX_ACCUMULATED_RAINFALL);
        markForSync();
    }

    public long getLastRainTick()
    {
        return lastRainTick;
    }

    public void setLastRainTick(long lastRainTick)
    {
        this.lastRainTick = lastRainTick;
        markForSync();
    }

    public enum NutrientType
    {
        NITROGEN, PHOSPHOROUS, POTASSIUM;

        public static final NutrientType[] VALUES = values();
    }
}
