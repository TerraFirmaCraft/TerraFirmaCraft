package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2i;

import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.calendar.Calendars;

public class EmiBasePotRecipe<T extends PotRecipe> extends GenericRecipe<T>
{
    public EmiBasePotRecipe(ResourceLocation id, T recipe, int width, int height)
    {
        super(EmiIntegration.POT, id, recipe, width, height);
        inputs.add(toIngredient(recipe.getFluidIngredient()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addText(Calendars.CLIENT.getTimeDelta(recipe.getDuration()), getDisplayWidth() / 2, getDisplayHeight(), 0xffffff, true).horizontalAlign(TextWidget.Alignment.CENTER).verticalAlign(TextWidget.Alignment.END);
        addInputWidgets(widgets);
        addOutputWidgets(widgets);

        widgets.addTexture(EmiTexture.EMPTY_FLAME, 68, 26);
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 68, 26, 8000, false, true, true);
    }

    protected void addInputWidgets(WidgetHolder widgets)
    {
        int index = 0;
        for (Vector2i pos : inputSlotPositions())
        {
            if (index < inputs.size())
            {
                widgets.addSlot(inputs.get(index), pos.x, pos.y);
            }
            else
            {
                widgets.addSlot(pos.x, pos.y);
            }
            index++;
        }
    }

    protected Vector2i[] inputSlotPositions()
    {
        return new Vector2i[] {
            // Fluid slot
            new Vector2i(24, 44),
            // Input slots
            new Vector2i(15, 6),
            new Vector2i(33, 6),
            new Vector2i(6, 24),
            new Vector2i(24, 24),
            new Vector2i(42, 24)
        };
    }

    protected void addOutputWidgets(WidgetHolder widgets)
    {
        Vector2i pos = outputSlotPosition();
        widgets.addSlot(outputs.getFirst(), pos.x, pos.y).recipeContext(this);
    }

    protected Vector2i outputSlotPosition()
    {
        return new Vector2i(90, 24);
    }
}
