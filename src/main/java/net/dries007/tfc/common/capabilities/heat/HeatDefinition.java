/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.common.ItemDefinition;

/**
 * This is a definition (reloaded via {@link HeatCapability}) of a heat that is applied to an item stack.
 */
public class HeatDefinition extends ItemDefinition
{
    private final Supplier<IHeat> capability;

    public HeatDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        float heatCapacity = JSONUtils.getAsFloat(json, "heat_capacity");
        float forgingTemp = JSONUtils.getAsFloat(json, "forging_temperature", 0);
        float weldingTemp = JSONUtils.getAsFloat(json, "welding_temperature", 0);

        this.capability = () -> new HeatHandler(heatCapacity, forgingTemp, weldingTemp);
    }

    /**
     * Creates a new instance of the capability defined by this object.
     */
    public IHeat create()
    {
        return capability.get();
    }
}
