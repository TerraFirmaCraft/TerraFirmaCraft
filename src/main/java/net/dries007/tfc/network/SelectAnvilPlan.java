/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;

public class SelectAnvilPlan
{
    private final ResourceLocation recipeId;

    public SelectAnvilPlan(AnvilRecipe recipe)
    {
        this.recipeId = recipe.getId();
    }

    SelectAnvilPlan(FriendlyByteBuf buffer)
    {
        this.recipeId = buffer.readResourceLocation();
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(recipeId);
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            if (player.containerMenu instanceof AnvilContainer anvilContainer)
            {
                AnvilRecipe recipe = Helpers.getRecipes(player.level(), TFCRecipeTypes.ANVIL).get(recipeId);
                anvilContainer.getBlockEntity().choseRecipe(recipe);
            }
        }
    }
}
