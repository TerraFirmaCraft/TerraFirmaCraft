/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public class BlockDefinition
{
    protected final ResourceLocation id;
    protected final BlockIngredient ingredient;

    public BlockDefinition(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.ingredient = BlockIngredient.fromJson(json.get("ingredient"));
    }

    public BlockDefinition(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;
        this.ingredient = BlockIngredient.fromNetwork(buffer);
    }

    public boolean matches(BlockState state)
    {
        return ingredient.test(state);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public BlockIngredient getIngredient()
    {
        return ingredient;
    }
}
