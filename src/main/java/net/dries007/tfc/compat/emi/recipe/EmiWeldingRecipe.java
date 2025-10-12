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
    public EmiWeldingRecipe(ResourceLocation id, WeldingRecipe recipe)
    {
        super(EmiIntegration.WELDING, id, recipe);
    }

    @Override
    protected void processRecipe()
    {
        inputs.add(EmiIngredient.of(recipe.getFirstInput()));
        inputs.add(EmiIngredient.of(recipe.getSecondInput()));
        inputs.add(EmiIngredient.of(TFCTags.Items.WELDING_FLUX));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }

    @Override
    protected SlotWidget generateOutputSlot(EmiStack stack, int x, int y, int index)
    {
        return super.generateOutputSlot(stack, x, y, index).appendTooltip(Component.translatable("tfc.tooltip.anvil_tier_required", Tooltips.tier(recipe.getTier())));
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiWeldingRecipe r)
        {
            return recipe.getTier() - r.recipe.getTier();
        }
        return super.compareTo(other);
    }
}
