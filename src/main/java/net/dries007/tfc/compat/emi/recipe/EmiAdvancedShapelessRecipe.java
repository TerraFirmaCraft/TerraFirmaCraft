/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.widgets.CyclingSlotWidget;
import net.dries007.tfc.compat.emi.widgets.ItemStackProviderWidget;

//TODO nothing visually changes for AddPowderModifier & AddBaitToRodModifier, is that fixable?
//they require that RecipeHelpers#setCraftingInput be set
public class EmiAdvancedShapelessRecipe extends EmiCraftingRecipe
{
    private final @Nullable EmiIngredient primaryIngredient;
    private final boolean isSpecial;
    private final ItemStackProvider provider;

    public EmiAdvancedShapelessRecipe(ResourceLocation id, AdvancedShapelessRecipe recipe)
    {
        super(recipe.getIngredients().stream().map(EmiIngredient::of).toList(), EmiHelpers.nonDecayStack(recipe.getResultItem(EmiHelpers.registryAccess())), id, true);
        primaryIngredient = recipe.getPrimaryIngredient().map(EmiIngredient::of).orElse(null);
        isSpecial = recipe.isSpecial();
        provider = recipe.getResult();
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        //Widget positions taken from EmiCraftingRecipe#addWidgets
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
        widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);
        SlotWidget primary = null;
        for (int i = 0; i < 9; i++)
        {
            if (i < input.size())
            {
                EmiIngredient value = input.get(i);
                SlotWidget slot = widgets.add(new CyclingSlotWidget(value, i, i % 3 * 18, i / 3 * 18));
                if (value.equals(primaryIngredient))
                {
                    primary = slot;
                }
            }
            else
            {
                widgets.addSlot(EmiStack.of(ItemStack.EMPTY), i % 3 * 18, i / 3 * 18);
            }
        }
        if (primary != null)
        {
            widgets.add(new ItemStackProviderWidget(primary, provider, 1523, 92, 14)).large(true).recipeContext(this);
        }
        else
        {
            widgets.addSlot(output, 92, 14).large(true).recipeContext(this);
        }
    }

    @Override
    public boolean supportsRecipeTree()
    {
        return !isSpecial;
    }
}
