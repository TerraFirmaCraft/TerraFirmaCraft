/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.JsonHelpers;

public record AddHeatModifier(float temperature) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        stack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.setTemperature(cap.getTemperature() + temperature));
        return stack;
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<AddHeatModifier>
    {
        INSTANCE;

        @Override
        public AddHeatModifier fromJson(JsonObject json)
        {
            final float temperature = JsonHelpers.getAsFloat(json, "temperature");
            return new AddHeatModifier(temperature);
        }

        @Override
        public AddHeatModifier fromNetwork(FriendlyByteBuf buffer)
        {
            final float temperature = buffer.readFloat();
            return new AddHeatModifier(temperature);
        }

        @Override
        public void toNetwork(AddHeatModifier modifier, FriendlyByteBuf buffer)
        {
            buffer.writeFloat(modifier.temperature);
        }
    }
}
