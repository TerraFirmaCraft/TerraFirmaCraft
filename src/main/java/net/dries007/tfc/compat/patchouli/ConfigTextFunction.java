/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.config.ItemTemperatureDisplayStyle;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.ClimateTemperatureDisplayStyle;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.tooltip.Tooltips;

/**
 * Registers the {@code $(cfg:<name>)} field guide text function, which substitutes a value derived from the server
 * config into book text.
 * <p>
 * {@link #VALUES} is the set of names the book is allowed to reference. Any name used in {@code generate_book.py}
 * must appear here.
 */
public final class ConfigTextFunction
{
    public static final String NAME = "cfg";

    private static final String TEMPERATURE = "temperature:";
    private static final String HEAT = "heat:";
    private static final Pattern REFERENCE = Pattern.compile("\\$\\(" + NAME + ":([^)]*)\\)");

    private static final Map<String, Supplier<Component>> VALUES = buildValues();

    @Nullable
    private static Component resolve(String param)
    {
        if (param.startsWith(TEMPERATURE))
        {
            final Float value = parse(param.substring(TEMPERATURE.length()));
            return value == null ? null : climateTemperature(value);
        }
        if (param.startsWith(HEAT))
        {
            final Float value = parse(param.substring(HEAT.length()));
            return value == null ? null : temperature(value.intValue());
        }
        final Supplier<Component> value = VALUES.get(param);
        return value != null ? value.get() : null;
    }

    @Nullable
    private static Float parse(String param)
    {
        try
        {
            return Float.parseFloat(param);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static Map<String, Supplier<Component>> buildValues()
    {
        final Map<String, Supplier<Component>> map = new HashMap<>();

        map.put("candleTicks", () -> duration(TFCConfig.SERVER.candleTicks.get()));
        map.put("torchTicks", () -> duration(TFCConfig.SERVER.torchTicks.get()));
        map.put("jackOLanternTicks", () -> duration(TFCConfig.SERVER.jackOLanternTicks.get()));
        map.put("pitKilnTicks", () -> duration(TFCConfig.SERVER.pitKilnTicks.get()));
        map.put("composterTicks", () -> duration(TFCConfig.SERVER.composterTicks.get()));
        map.put("pitKilnTemperature", () -> temperature(TFCConfig.SERVER.pitKilnTemperature.get()));
        map.put("pitKilnHeat", () -> heat(TFCConfig.SERVER.pitKilnTemperature.get()));
        map.put("jugCapacity", () -> Tooltips.fluidUnits(TFCConfig.SERVER.jugCapacity.get()));
        map.put("woodenBucketCapacity", () -> Tooltips.fluidUnits(TFCConfig.SERVER.woodenBucketCapacity.get()));
        map.put("metalBucketCapacity", () -> Tooltips.fluidUnits(TFCConfig.SERVER.metalBucketCapacity.get()));
        map.put("bloomeryCapacity", () -> count(TFCConfig.SERVER.bloomeryCapacity.get()));
        map.put("bloomeryTotalCapacity", () -> count(TFCConfig.SERVER.bloomeryCapacity.get() * TFCConfig.SERVER.bloomeryMaxChimneyHeight.get()));
        map.put("traitPreservedModifier", () -> decayMultiplier(TFCConfig.SERVER.traitPreservedModifier.get()));
        map.put("traitWoodGrilledModifier", () -> decayMultiplier(TFCConfig.SERVER.traitWoodGrilledModifier.get()));
        map.put("traitCharcoalGrilledModifier", () -> decayMultiplier(TFCConfig.SERVER.traitCharcoalGrilledModifier.get()));

        for (Crop crop : Crop.values())
        {
            putClimate(map, crop.getSerializedName(), ClimateRanges.CROPS.get(crop));
        }
        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            putClimate(map, tree.name().toLowerCase(Locale.ROOT), ClimateRanges.FRUIT_TREES.get(tree));
        }
        for (FruitBlocks.SpreadingBush bush : FruitBlocks.SpreadingBush.values())
        {
            putClimate(map, bush.name().toLowerCase(Locale.ROOT), ClimateRanges.SPREADING_BUSHES.get(bush));
        }
        for (FruitBlocks.StationaryBush bush : FruitBlocks.StationaryBush.values())
        {
            putClimate(map, bush.name().toLowerCase(Locale.ROOT), ClimateRanges.STATIONARY_BUSHES.get(bush));
        }
        putClimate(map, "banana", ClimateRanges.BANANA_PLANT);
        putClimate(map, "cranberry", ClimateRanges.CRANBERRY_BUSH);

        return Map.copyOf(map);
    }

    private static void putClimate(Map<String, Supplier<Component>> map, String name, DataManager.Reference<ClimateRange> range)
    {
        final String prefix = "climate." + name + ".";
        assert !map.containsKey(prefix + "temperature") : "Duplicate climate range name: " + name;
        map.put(prefix + "temperature", () -> range(climateTemperature(range.get().getMinTemperature(false)), climateTemperature(range.get().getMaxTemperature(false))));
        map.put(prefix + "hydration", () -> range(range.get().getMinHydration(false), range.get().getMaxHydration(false)));
    }

    /**
     * Used in places where patchy's text parser isn't run (like in some custom components)
     */
    public static Component substitute(Component text)
    {
        final String value = text.getString();
        final Matcher matcher = REFERENCE.matcher(value);
        if (!matcher.find())
        {
            return text;
        }

        final MutableComponent result = Component.empty().withStyle(text.getStyle());
        int last = 0;
        do
        {
            result.append(value.substring(last, matcher.start()));
            result.append(unknownOr(resolve(matcher.group(1)), matcher.group(1)));
            last = matcher.end();
        }
        while (matcher.find());
        result.append(value.substring(last));
        return result;
    }

    private static Component unknownOr(@Nullable Component value, String param)
    {
        return value != null ? value : Component.literal("[UNKNOWN CONFIG: " + param + "]");
    }

    /**
     * When evaluated during datagen this should return fallback default config values (which is what we want)
     */
    public static Map<String, Supplier<Component>> values()
    {
        return VALUES;
    }

    public static void register()
    {
        PatchouliAPI.get().registerFunction(NAME, (param, style) -> {
            final Component component = unknownOr(resolve(param), param);
            // The book renders text, not components, so any color the value carries has to be moved onto the span
            final TextColor color = component.getStyle().getColor();
            if (color != null)
            {
                style.modifyStyle(s -> s.withColor(color));
            }
            return component.getString();
        });
    }

    /**
     * Convert player ticks -> raw count of calendar days
     */
    private static Component duration(int playerTicks)
    {
        if (playerTicks <= 0)
        {
            return Component.translatable("tfc.field_guide.indefinitely");
        }
        // Round rather than floor, as getTotalCalendarDays() would. The tick rate is a float, so a whole number of
        // days lands just under the boundary, and ten days would otherwise read as nine
        final long calendarTicks = Calendars.CLIENT.getFixedCalendarTicksFromTick(playerTicks);
        return calendarTicks >= ICalendar.CALENDAR_TICKS_IN_DAY
            ? Component.translatable("tfc.tooltip.time_delta_days", Math.round((double) calendarTicks / ICalendar.CALENDAR_TICKS_IN_DAY))
            : Component.translatable("tfc.field_guide.hours", Math.round((double) calendarTicks / ICalendar.CALENDAR_TICKS_IN_HOUR));
    }

    private static Component heat(int degrees)
    {
        final Heat heat = Heat.getHeat(degrees);
        return heat == null ? Component.empty() : Helpers.translateEnum(heat).withStyle(heat.getColor());
    }

    /**
     * Climate temperatures, unlike the heat of an item, are frequently negative, so they use {@code formatRange},
     * which does not treat that as absent.
     */
    private static Component climateTemperature(float value)
    {
        final Component formatted = TFCConfig.CLIENT.climateTooltipStyle.get().formatRange(value);
        return formatted != null ? formatted : Objects.requireNonNull(ClimateTemperatureDisplayStyle.CELSIUS.formatRange(value));
    }

    private static Component range(Object min, Object max)
    {
        return Component.translatable("tfc.field_guide.range", min, max);
    }

    private static Component count(int amount)
    {
        return Component.literal(String.valueOf(amount));
    }

    private static Component temperature(int degrees)
    {
        final ItemTemperatureDisplayStyle style = TFCConfig.CLIENT.heatTooltipStyle.get();
        final Component formatted = (style == ItemTemperatureDisplayStyle.COLOR ? ItemTemperatureDisplayStyle.CELSIUS : style).format(degrees);
        return formatted == null ? count(degrees) : formatted;
    }

    /**
     * Book prefers to talk about modifiers as rates, so 0.8 is "1.25x longer"
     */
    private static Component decayMultiplier(double modifier)
    {
        if (modifier <= 0) // Food with this trait never decays
        {
            return Component.literal("∞");
        }
        final double factor = 1 / modifier;
        return Component.literal(factor == Math.rint(factor)
            ? String.valueOf((long) factor)
            : String.format(Locale.ROOT, "%.2f", factor).replaceAll("0+$", ""));
    }
}
