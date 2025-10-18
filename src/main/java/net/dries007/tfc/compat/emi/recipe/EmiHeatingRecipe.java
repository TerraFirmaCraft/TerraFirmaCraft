/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.AnimatedTextureWidget;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.TextureWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.config.TFCConfig;

public class EmiHeatingRecipe extends AutoLayoutRecipe<HeatingRecipe>
{
    private static final EmiTexture emptyFlame = EmiTexture.EMPTY_FLAME;
    private static final EmiTexture fullFlame = EmiTexture.FULL_FLAME;

    protected final float temperature;
    protected final boolean outputsSolid;

    public EmiHeatingRecipe(ResourceLocation id, HeatingRecipe recipe)
    {
        super(EmiIntegration.HEATING, id, recipe);
        temperature = recipe.getTemperature();
        outputsSolid = !recipe.getResultItem(EmiHelpers.registryAccess()).isEmpty() && recipe.getDisplayOutputFluid().isEmpty();
        init(recipe);
    }

    @Override
    protected void processRecipe(HeatingRecipe recipe)
    {
        inputs.add(EmiIngredient.of(recipe.getIngredient()));

        ItemStack itemOut = recipe.getResultItem(EmiHelpers.registryAccess());
        FluidStack fluidOut = recipe.getDisplayOutputFluid();
        if (!itemOut.isEmpty())
        {
            outputs.add(EmiHelpers.nonDecayStack(itemOut));
        }
        if (!fluidOut.isEmpty())
        {
            outputs.add(EmiStack.of(fluidOut.getFluid(), fluidOut.getAmount()));
        }
    }

    @Override
    protected List<Widget> generateWidgets(HeatingRecipe recipe)
    {
        WidgetLayout widgets = new WidgetLayout(new Bounds(getMargin() + getPaddingLeft(), getMargin() + getPaddingTop(), 0, 0));
        widgets.add(new SlotWidget(inputs.getFirst(), widgets.last(Position.X), widgets.last(Position.Y)));
        widgets.add(new TextureWidget(
            emptyFlame.texture,
            widgets.last(Position.RIGHT, 4),
            widgets.last(Position.Y),
            emptyFlame.width,
            emptyFlame.height,
            emptyFlame.u,
            emptyFlame.v
        ));
        widgets.add(new AnimatedTextureWidget(
            fullFlame.texture,
            widgets.last(Position.X),
            widgets.last(Position.Y),
            fullFlame.width,
            fullFlame.height,
            fullFlame.u,
            fullFlame.v,
            8000,
            false,
            true,
            true
        ));

        EmiStack output = outputs.isEmpty() ? EmiStack.EMPTY : outputs.getLast();
        widgets.add(new SlotWidget(output, widgets.last(Position.RIGHT, 4), widgets.index(0, Position.Y)).recipeContext(this));
        return widgets;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        super.addWidgets(widgets);

        Component text = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (text != null)
        {
            widgets.addText(text, getDisplayWidth() / 2, getMargin() + 2, 0xff000000, true)
                .horizontalAlign(TextWidget.Alignment.CENTER)
                .verticalAlign(TextWidget.Alignment.CENTER);
        }
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiHeatingRecipe heating)
        {
            return (int) (temperature - heating.temperature);
        }
        return super.compareTo(other);
    }

    public boolean hasSolidOutput()
    {
        return outputsSolid;
    }

    @Override
    protected int getPaddingTop()
    {
        return 10;
    }

    @Override
    protected int getPaddingLeft()
    {
        return 10;
    }

    @Override
    protected int getPaddingRight()
    {
        return 10;
    }
}
