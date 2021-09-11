/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.util.ItemDefinition;

/**
 * This is a definition (reloaded via {@link HeatCapability}) of a heat that is applied to an item stack.
 */
public class HeatDefinition extends ItemDefinition
{
    private final Supplier<ICapabilityProvider> capability;

    public HeatDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        float heatCapacity = GsonHelper.getAsFloat(json, "heat_capacity");
        float forgingTemp = GsonHelper.getAsFloat(json, "forging_temperature", 0);
        float weldingTemp = GsonHelper.getAsFloat(json, "welding_temperature", 0);

        this.capability = () -> new HeatHandler(heatCapacity, forgingTemp, weldingTemp);
    }

    /**
     * Creates a new instance of the capability defined by this object.
     */
    public ICapabilityProvider create()
    {
        return capability.get();
    }
}
