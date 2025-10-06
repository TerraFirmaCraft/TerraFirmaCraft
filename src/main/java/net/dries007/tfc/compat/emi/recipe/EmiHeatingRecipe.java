package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.config.TFCConfig;

public class EmiHeatingRecipe extends GenericRecipe<HeatingRecipe>
{
    // Positions and dimensions taken from the JEI HeatingRecipeCategory
    public EmiHeatingRecipe(ResourceLocation id, HeatingRecipe recipe)
    {
        super(EmiIntegration.HEATING, id, recipe, 120, 38);

        inputs.add(EmiIngredient.of(recipe.getIngredient()));

        ItemStack itemOut = recipe.getResultItem(registryAccess());
        FluidStack fluidOut = recipe.getDisplayOutputFluid();
        if (!itemOut.isEmpty())
        {
            outputs.add(EmiStack.of(itemOut));
        }
        if (!fluidOut.isEmpty())
        {
            outputs.add(EmiStack.of(fluidOut.getFluid(), fluidOut.getAmount()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.getFirst(), 21, 17);
        widgets.addSlot(outputs.getFirst(), 85, 17).recipeContext(this);

        widgets.addTexture(EmiTexture.EMPTY_FLAME, 54, 19);
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 54, 19, 8000, false, true, true);

        MutableComponent color = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
        if (color != null)
        {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            widgets.addText(color, getDisplayWidth() / 2 - font.width(color) / 2, 4, 0xFFFFFF, true);
        }
    }
}
