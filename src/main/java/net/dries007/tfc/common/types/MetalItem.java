/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class MetalItem
{
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final Metal metal;
    private final int amount;

    public MetalItem(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        ingredient = CraftingHelper.getIngredient(JSONUtils.getAsJsonObject(json, "ingredient"));
        ResourceLocation metalId = new ResourceLocation(JSONUtils.getAsString(json, "metal"));
        metal = MetalManager.INSTANCE.get(metalId);
        if (metal == null)
        {
            throw new JsonSyntaxException("Invalid metal specified: " + metalId.toString());
        }
        amount = JSONUtils.getAsInt(json, "amount");
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Metal getMetal()
    {
        return metal;
    }

    public int getAmount()
    {
        return amount;
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public boolean isValid(ItemStack stack)
    {
        return this.ingredient.test(stack);
    }
}