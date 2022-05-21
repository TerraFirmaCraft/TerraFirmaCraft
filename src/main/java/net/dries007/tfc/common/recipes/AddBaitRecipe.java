/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class AddBaitRecipe extends AdvancedShapelessRecipe
{
    public AddBaitRecipe(ResourceLocation id, String group, ItemStackProvider result, NonNullList<Ingredient> ingredients, Ingredient primaryIngredient)
    {
        super(id, group, result, ingredients, primaryIngredient);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level)
    {
        if (super.matches(inv, level))
        {
            boolean foundBait = false;
            boolean foundRod = false;
            for (int i = 0; i < inv.getContainerSize(); i++)
            {
                ItemStack item = inv.getItem(i);
                if (TFCFishingRodItem.getBaitType(item) != TFCFishingRodItem.BaitType.NONE)
                {
                    if (foundBait)
                    {
                        return false; // cannot have two baits
                    }
                    else
                    {
                        foundBait = true;
                    }
                }
                if (item.getItem() instanceof TFCFishingRodItem)
                {
                    if (foundRod)
                    {
                        return false; // cannot have two rods
                    }
                    else
                    {
                        foundRod = true;
                        if (!TFCFishingRodItem.getBaitItem(item).isEmpty())
                        {
                            return false; // cannot already have bait
                        }
                    }
                }
            }
            return foundRod && foundBait;
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv)
    {
        ItemStack assembled = super.assemble(inv);
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack item = inv.getItem(i);
            TFCFishingRodItem.BaitType baitType = TFCFishingRodItem.getBaitType(item);
            if (baitType != TFCFishingRodItem.BaitType.NONE)
            {
                assembled.getOrCreateTag().put("bait", item.save(new CompoundTag()));
                return assembled;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ADD_BAIT_CRAFTING.get();
    }
}
