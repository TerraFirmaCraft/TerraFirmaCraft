package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.tooltip.Tooltips;

public class EmiAnvilRecipe extends GenericRecipe<AnvilRecipe>
{
    public EmiAnvilRecipe(ResourceLocation id, AnvilRecipe recipe)
    {
        super(EmiIntegration.ANVIL, id, recipe, 98, 26);
        inputs.add(EmiIngredient.of(recipe.getInput()));
        outputs.add(EmiStack.of(recipe.getResultItem(registryAccess())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.getFirst(), 6, 5);
        widgets.addSlot(outputs.getFirst(), 76, 5).recipeContext(this).appendTooltip(Component.translatable("tfc.tooltip.anvil_tier_required", Tooltips.tier(recipe.getMinTier())));
        widgets.addFillingArrow(36, 5, 3000);
    }

    public static RegistryAccess registryAccess()
    {
        return ClientHelpers.getLevelOrThrow().registryAccess();
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
