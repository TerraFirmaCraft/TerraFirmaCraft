/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.calendar.Calendars;

public class EmiBasePotRecipe<T extends PotRecipe> extends BasicRecipe<T>
{
    private final int duration;
    private final Vector2i[] SLOT_POSITIONS = new Vector2i[] {
        // Fluid slot
        new Vector2i(24, 44),
        // Input slots
        new Vector2i(15, 6),
        new Vector2i(33, 6),
        new Vector2i(6, 24),
        new Vector2i(24, 24),
        new Vector2i(42, 24)
    };

    public EmiBasePotRecipe(ResourceLocation id, T recipe, int width, int height)
    {
        super(EmiIntegration.POT, id, width, height);
        inputs.add(EmiHelpers.toIngredient(recipe.getFluidIngredient()));
        duration = recipe.getDuration();
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addText(Calendars.CLIENT.getTimeDelta(duration), getDisplayWidth() / 2, getDisplayHeight(), 0xffffff, true)
            .horizontalAlign(TextWidget.Alignment.CENTER)
            .verticalAlign(TextWidget.Alignment.END);
        addInputWidgets(widgets);
        addOutputWidgets(widgets);

        widgets.addTexture(EmiTexture.EMPTY_FLAME, 68, 26);
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 68, 26, 8000, false, true, true);
    }

    protected void addInputWidgets(WidgetHolder widgets)
    {
        int index = 0;
        for (Vector2i pos : SLOT_POSITIONS)
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

    protected void addOutputWidgets(WidgetHolder widgets)
    {
        int index = 0;
        for (Vector2i pos : outputSlotPositions())
        {
            if (index < inputs.size())
            {
                widgets.addSlot(outputs.get(index), pos.x, pos.y).recipeContext(this);
            }
            else
            {
                widgets.addSlot(pos.x, pos.y);
            }
            index++;
        }
    }

    protected Vector2i[] outputSlotPositions()
    {
        int count = getOutputs().size();
        int distancePerStep = 20;
        int heightTotal = count * distancePerStep;
        int yOff = TextWidget.Alignment.CENTER.offset(heightTotal);
        List<Vector2i> points = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            points.add(new Vector2i(90, 34 + yOff + distancePerStep * i));
        }
        return points.toArray(Vector2i[]::new);
    }

    protected static <T, V> List<V> groupSimilar(List<T> list, BiFunction<T, Integer, V> mapper, BiFunction<T, T, Boolean> comp)
    {
        List<T> ordered = new ArrayList<>();
        HashMap<T, Integer> stacked = new HashMap<>();
        for (T entry : list)
        {
            boolean found = false;
            T target = entry;
            for (T existing : ordered)
            {
                if (comp.apply(existing, entry))
                {
                    found = true;
                    target = existing;
                    break;
                }
            }
            if (!found)
            {
                ordered.add(entry);
            }
            stacked.compute(target, (x, i) -> i == null ? 1 : i + 1);
        }
        return ordered.stream().map(i -> mapper.apply(i, stacked.get(i))).toList();
    }
}
