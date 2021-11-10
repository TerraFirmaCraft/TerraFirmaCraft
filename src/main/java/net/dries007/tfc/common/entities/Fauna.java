/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.RegisteredDataManager;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.decorator.ClimateConfig;

/**
 * A data driven way to make spawning conditions for animals player configurable.
 */
public class Fauna
{
    public static final RegisteredDataManager<Fauna> MANAGER = new RegisteredDataManager<>(Fauna::new, Fauna::new, "fauna", "fauna");

    private final ResourceLocation id;
    private final int chance;
    private final int distanceBelowSeaLevel;
    private final ClimateConfig climateConfig;
    private final boolean solidGround;

    public Fauna(ResourceLocation id)
    {
        this.id = id;
        this.chance = 1;
        this.distanceBelowSeaLevel = -1;
        this.climateConfig = new ClimateConfig(0, 0, 0, 0, ForestType.NONE, ForestType.NONE, false);
        this.solidGround = false;
    }

    public Fauna(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.chance = JsonHelpers.getAsInt(json, "chance", 1);
        this.distanceBelowSeaLevel = JsonHelpers.getAsInt(json, "distance_below_sea_level", -1);
        this.climateConfig = JsonHelpers.decodeCodec(json, ClimateConfig.CODEC, "climate");
        this.solidGround = JsonHelpers.getAsBoolean(json, "solid_ground", false);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public int getChance()
    {
        return chance;
    }

    public int getDistanceBelowSeaLevel()
    {
        return distanceBelowSeaLevel;
    }

    public ClimateConfig getClimateConfig()
    {
        return climateConfig;
    }

    public boolean isSolidGround()
    {
        return solidGround;
    }
}
