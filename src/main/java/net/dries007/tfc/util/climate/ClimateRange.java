/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.RegisteredDataManager;
import org.jetbrains.annotations.NotNull;

public class ClimateRange
{
    public static final RegisteredDataManager<ClimateRange> MANAGER = new RegisteredDataManager<>(ClimateRange::new, ClimateRange::new, Helpers.identifier("climate_ranges"), "climate range", ClimateRange::new, ClimateRange::encode, Packet::new);
    public static final ClimateRange NOOP = new ClimateRange(new ResourceLocation("tfc:no_op"));

    private final ResourceLocation id;
    private final int minHydration, maxHydration, hydrationWiggleRange; // Hydration = a hybrid of nearby water and rainfall
    private final float minTemperature, maxTemperature, temperatureWiggleRange; // Temperature = in-world temperature

    private ClimateRange(ResourceLocation id)
    {
        this.id = id;
        this.minHydration = 0;
        this.maxHydration = 100;
        this.hydrationWiggleRange = 0;
        this.minTemperature = -100;
        this.maxTemperature = 100;
        this.temperatureWiggleRange = 0;
    }

    private ClimateRange(ResourceLocation id, JsonObject json)
    {
        this.id = id;

        this.minHydration = JsonHelpers.getAsInt(json, "min_hydration", 0);
        this.maxHydration = JsonHelpers.getAsInt(json, "max_hydration", 100);
        this.hydrationWiggleRange = JsonHelpers.getAsInt(json, "hydration_wiggle_range", 0);

        this.minTemperature = JsonHelpers.getAsFloat(json, "min_temperature", -100);
        this.maxTemperature = JsonHelpers.getAsFloat(json, "max_temperature", 100);
        this.temperatureWiggleRange = JsonHelpers.getAsFloat(json, "temperature_wiggle_range", 0);
    }

    private ClimateRange(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;

        this.minHydration = buffer.readVarInt();
        this.maxHydration = buffer.readVarInt();
        this.hydrationWiggleRange = buffer.readVarInt();

        this.minTemperature = buffer.readFloat();
        this.maxTemperature = buffer.readFloat();
        this.temperatureWiggleRange = buffer.readFloat();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(minHydration);
        buffer.writeVarInt(maxHydration);
        buffer.writeVarInt(hydrationWiggleRange);

        buffer.writeFloat(minTemperature);
        buffer.writeFloat(maxTemperature);
        buffer.writeFloat(temperatureWiggleRange);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public int getMinHydration(boolean allowWiggle)
    {
        return allowWiggle ? minHydration - hydrationWiggleRange : minHydration;
    }

    public int getMaxHydration(boolean allowWiggle)
    {
        return allowWiggle ? maxHydration + hydrationWiggleRange : maxHydration;
    }

    public float getMinTemperature(boolean allowWiggle)
    {
        return allowWiggle ? minTemperature - temperatureWiggleRange : minTemperature;
    }

    public float getMaxTemperature(boolean allowWiggle)
    {
        return allowWiggle ? maxTemperature + temperatureWiggleRange : maxTemperature;
    }

    public Result checkHydration(int hydration, boolean allowWiggle)
    {
        return check(hydration, minHydration, maxHydration, hydrationWiggleRange, allowWiggle);
    }

    public Result checkTemperature(float temperature, boolean allowWiggle)
    {
        return check(temperature, minTemperature, maxTemperature, temperatureWiggleRange, allowWiggle);
    }

    public boolean checkBoth(int hydration, float temperature, boolean allowWiggle)
    {
        return checkHydration(hydration, allowWiggle) == Result.VALID && checkTemperature(temperature, allowWiggle) == Result.VALID;
    }

    @NotNull
    private Result check(float value, float min, float max, float range, boolean allowRange)
    {
        if (allowRange)
        {
            min -= range;
            max += range;
        }
        if (value < min)
        {
            return Result.LOW;
        }
        if (value > max)
        {
            return Result.HIGH;
        }
        return Result.VALID;
    }

    public enum Result
    {
        LOW, VALID, HIGH
    }

    public static class Packet extends DataManagerSyncPacket<RegisteredDataManager.Entry<ClimateRange>> {}
}
