/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.tooltip.Tooltips;

public class EmiWeldingRecipe extends AutoLayoutRecipe<WeldingRecipe>
{
    private final int tier;

    public EmiWeldingRecipe(ResourceLocation id, WeldingRecipe recipe)
    {
        super(EmiIntegration.WELDING, id, recipe);
        tier = recipe.getTier();
        init(recipe);
    }

    @Override
    protected void processRecipe(WeldingRecipe recipe)
    {
        inputs.add(EmiIngredient.of(recipe.getFirstInput()));
        inputs.add(EmiIngredient.of(recipe.getSecondInput()));
        inputs.add(EmiIngredient.of(TFCTags.Items.WELDING_FLUX));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }

    @Override
    protected SlotWidget generateOutputSlot(EmiStack stack, int x, int y, int index)
    {
        return super.generateOutputSlot(stack, x, y, index)
            .appendTooltip(Component.translatable("tfc.tooltip.anvil_tier_required", Tooltips.tier(tier)));
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiWeldingRecipe r)
        {
            int tierDiff = tier - r.tier;
            if (tierDiff != 0)
            {
                return tier - r.tier;
            }
        }
        return super.compareTo(other);
    }
}
