/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public record RecipeSerializerImpl<R extends Recipe<?>>(
    MapCodec<R> codec,
    StreamCodec<RegistryFriendlyByteBuf, R> streamCodec
) implements RecipeSerializer<R>
{
    public RecipeSerializerImpl(R singleInstance)
    {
        this(MapCodec.unit(singleInstance), StreamCodec.unit(singleInstance));
    }
}