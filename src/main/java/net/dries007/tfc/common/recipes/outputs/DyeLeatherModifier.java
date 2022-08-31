/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.*;

import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public record DyeLeatherModifier(DyeColor color) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        DyeableLeatherItem.dyeArmor(stack, List.of(DyeItem.byColor(color)));
        return stack;
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<DyeLeatherModifier>
    {
        INSTANCE;

        @Nullable
        private static DyeColor byName(String name)
        {
            for (DyeColor color : DyeColor.values())
            {
                if (color.getName().equals(name))
                {
                    return color;
                }
            }
            return null;
        }

        @Override
        public DyeLeatherModifier fromJson(JsonObject json)
        {
            final String name = JsonHelpers.getAsString(json, "color");
            DyeColor dye = byName(name);
            if (dye != null)
            {
                return new DyeLeatherModifier(dye);
            }
            throw new JsonSyntaxException("Not a dye color: " + name + ", must be one of: " + Arrays.toString(DyeColor.values()));
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
