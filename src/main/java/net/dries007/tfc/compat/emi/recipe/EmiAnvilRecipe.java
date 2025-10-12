package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.tooltip.Tooltips;

public class EmiAnvilRecipe extends AutoLayoutRecipe<AnvilRecipe>
{
    public EmiAnvilRecipe(ResourceLocation id, AnvilRecipe recipe)
    {
        super(EmiIntegration.ANVIL, id, recipe);
    }

    @Override
    protected void processRecipe()
    {
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }

    @Override
    protected SlotWidget generateOutputSlot(EmiStack stack, int x, int y, int index)
    {
        return super.generateOutputSlot(stack, x, y, index).appendTooltip(Component.translatable("tfc.tooltip.anvil_tier_required", Tooltips.tier(recipe.getMinTier())));
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiAnvilRecipe r)
        {
            return recipe.getMinTier() - r.recipe.getMinTier();
        }
        return super.compareTo(other);
    }
}
