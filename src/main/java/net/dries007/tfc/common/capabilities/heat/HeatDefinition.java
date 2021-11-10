/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.util.ItemDefinition;

/**
 * This is a definition (reloaded via {@link HeatCapability}) of a heat that is applied to an item stack.
 */
public class HeatDefinition extends ItemDefinition
{
    private final float heatCapacity;
    private final float forgingTemp;
    private final float weldingTemp;

    public HeatDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        heatCapacity = GsonHelper.getAsFloat(json, "heat_capacity");
        forgingTemp = GsonHelper.getAsFloat(json, "forging_temperature", 0);
        weldingTemp = GsonHelper.getAsFloat(json, "welding_temperature", 0);
    }

    public HeatDefinition(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));

        heatCapacity = buffer.readFloat();
        forgingTemp = buffer.readFloat();
        weldingTemp = buffer.readFloat();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);

        buffer.writeFloat(heatCapacity);
        buffer.writeFloat(forgingTemp);
        buffer.writeFloat(weldingTemp);
    }

    public ICapabilityProvider create()
    {
        return new HeatHandler(heatCapacity, forgingTemp, weldingTemp);
    }
}
