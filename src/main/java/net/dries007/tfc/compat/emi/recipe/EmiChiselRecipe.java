/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiChiselRecipe extends BasicRecipe<ChiselRecipe>
{
    private final ChiselMode mode;

    public EmiChiselRecipe(ResourceLocation id, ChiselRecipe recipe)
    {
        super(EmiIntegration.CHISEL, id, 118, 26);
        mode = recipe.getMode();

        inputs.add(EmiHelpers.toIngredient(recipe.getIngredient()));
        inputs.add(EmiIngredient.of(TFCTags.Items.TOOLS_CHISEL));
        outputs.add(EmiStack.of(recipe.getResultItem(null)));
        outputs.add(EmiStack.of(recipe.getItemOutput(ItemStack.EMPTY)));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.get(0), 6, 5);
        widgets.addSlot(inputs.get(1), 26, 5);
        widgets.addSlot(outputs.get(0), 76, 5).recipeContext(this);
        widgets.addSlot(outputs.get(1), 96, 5).recipeContext(this);
        mode.createIcon((id, u, v, width, height) -> widgets.addTexture(id, 50, 3, width, height, u, v));
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiChiselRecipe chisel)
        {
            ResourceLocation modeA = ChiselMode.REGISTRY.getKey(mode);
            ResourceLocation modeB = ChiselMode.REGISTRY.getKey(chisel.mode);
            return modeA.compareTo(modeB);
        }
        return super.compareTo(other);
    }
}
