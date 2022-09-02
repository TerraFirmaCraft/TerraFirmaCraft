/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.Collections;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.*;

import net.dries007.tfc.util.JsonHelpers;

public record DyeLeatherModifier(DyeColor color) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return DyeableLeatherItem.dyeArmor(stack, Collections.singletonList(DyeItem.byColor(color)));
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<DyeLeatherModifier>
    {
        INSTANCE;

        @Override
        public DyeLeatherModifier fromJson(JsonObject json)
        {
            return new DyeLeatherModifier(JsonHelpers.getDyeColor(json, "color"));
        }

        @Override
        public DyeLeatherModifier fromNetwork(FriendlyByteBuf buffer)
        {
            return new DyeLeatherModifier(buffer.readEnum(DyeColor.class));
        }

        @Override
        public void toNetwork(DyeLeatherModifier modifier, FriendlyByteBuf buffer)
        {
            buffer.writeEnum(modifier.color);
        }
    }
}
