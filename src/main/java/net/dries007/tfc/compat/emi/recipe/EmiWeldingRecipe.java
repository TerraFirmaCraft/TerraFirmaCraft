package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.tooltip.Tooltips;

public class EmiWeldingRecipe extends GenericRecipe<WeldingRecipe>
{
    public EmiWeldingRecipe(ResourceLocation id, WeldingRecipe recipe)
    {
        super(EmiIntegration.WELDING, id, recipe, 118, 26);

        inputs.add(EmiIngredient.of(recipe.getFirstInput()));
        inputs.add(EmiIngredient.of(recipe.getSecondInput()));
        inputs.add(EmiIngredient.of(TFCTags.Items.WELDING_FLUX));
        outputs.add(EmiStack.of(recipe.getResultItem(registryAccess())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.get(0), 6, 5);
        widgets.addSlot(inputs.get(1), 26, 5);
        widgets.addSlot(inputs.get(2), 46, 5);
        widgets.addSlot(outputs.getFirst(), 96, 5).recipeContext(this).appendTooltip(Component.translatable("tfc.tooltip.anvil_tier_required", Tooltips.tier(recipe.getTier())));
        widgets.addFillingArrow(68, 5, 3000);
    }
}
