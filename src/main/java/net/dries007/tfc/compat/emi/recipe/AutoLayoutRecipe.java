/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.FillingArrowWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public abstract class AutoLayoutRecipe<T extends Recipe<?>> implements EmiRecipe, ComparableRecipe
{

    private final ResourceLocation id;
    private final EmiRecipeCategory category;
    protected final List<EmiIngredient> inputs = new ArrayList<>();
    protected final List<EmiIngredient> catalysts = new ArrayList<>();
    protected final List<EmiStack> outputs = new ArrayList<>();

    private Widget[] generatedWidgets;
    protected int width;
    protected int height;

    public AutoLayoutRecipe(EmiRecipeCategory category, ResourceLocation id, T recipe)
    {
        this.id = id;
        this.category = category;
    }

    protected void init(T recipe)
    {
        processRecipe(recipe);
        generatedWidgets = generateWidgets(recipe).toArray(Widget[]::new);
        int w = 0;
        int h = 0;
        for (Widget widget : generatedWidgets)
        {
            Bounds bounds = widget.getBounds();
            w = Math.max(w, bounds.right());
            h = Math.max(h, bounds.bottom());
        }
        width = w + getMargin() + getPaddingRight();
        height = h + getMargin() + getPaddingBottom();
    }

    @Override
    public EmiRecipeCategory getCategory()
    {
        return category;
    }

    @Override
    public @Nullable ResourceLocation getId()
    {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs()
    {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs()
    {
        return outputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts()
    {
        return catalysts;
    }

    @Override
    public int getDisplayWidth()
    {
        return width;
    }

    @Override
    public int getDisplayHeight()
    {
        return height;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        for (Widget widget : generatedWidgets)
        {
            widgets.add(widget);
        }
    }

    protected abstract void processRecipe(T recipe);

    protected List<Widget> generateWidgets(T recipe)
    {
        List<Widget> widgets = new ArrayList<>();
        int x = getMargin() + getPaddingLeft();
        int y = getMargin() + getPaddingTop();

        int index = 0;
        for (EmiIngredient ingredient : getInputs())
        {
            Widget slot = generateInputSlot(ingredient, x, y, index);
            Bounds bounds = slot.getBounds();
            x = bounds.right() + 3;
            widgets.add(slot);
            index++;
        }

        Widget middle = generateMiddleWidget(x, y);
        widgets.add(middle);
        x = middle.getBounds().right() + 3;

        index = 0;
        for (EmiStack stack : getOutputs())
        {
            Widget slot = generateOutputSlot(stack, x, y, index);
            Bounds bounds = slot.getBounds();
            x = bounds.right() + 3;
            widgets.add(slot);
            index++;
        }
        return widgets;
    }

    protected static Widget generateMiddleWidget(int x, int y)
    {
        return new FillingArrowWidget(x, y, 3000);
    }

    protected SlotWidget generateInputSlot(EmiIngredient ingredient, int x, int y, int index)
    {
        return new SlotWidget(ingredient, x, y);
    }

    protected SlotWidget generateOutputSlot(EmiStack stack, int x, int y, int index)
    {
        return new SlotWidget(stack, x, y).recipeContext(this);
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        ResourceLocation otherId = other.getId();
        if (otherId != null)
        {
            return id.compareTo(other.getId());
        }
        return 0;
    }

    protected int getMargin()
    {
        return 4;
    }

    protected int getPaddingTop()
    {
        return 0;
    }

    protected int getPaddingBottom()
    {
        return 0;
    }

    protected int getPaddingLeft()
    {
        return 0;
    }

    protected int getPaddingRight()
    {
        return 0;
    }

    /**
     * Utility class to make calculating widget positions a little easier
     */
    public static class WidgetLayout extends ArrayList<Widget>
    {
        private final Bounds initialPos;

        public WidgetLayout()
        {
            super();
            initialPos = new Bounds(0, 0, 0, 0);
        }

        public WidgetLayout(Bounds bounds)
        {
            super();
            initialPos = bounds;
        }

        public int last(Position p, int offset)
        {
            if (isEmpty())
            {
                return p.apply(initialPos) + offset;
            }
            return p.apply(getLast().getBounds()) + offset;
        }

        public int last(Position p)
        {
            return last(p, 0);
        }

        public int index(int index, Position p, int offset)
        {
            if (isEmpty())
            {
                return p.apply(initialPos) + offset;
            }
            return p.apply(get(index).getBounds()) + offset;
        }

        public int index(int index, Position p)
        {
            return index(index, p, 0);
        }

        public int max(Position p)
        {
            return stream().map(Widget::getBounds).mapToInt(p::apply).max().orElse(0);
        }

        public <T extends Widget> T addWidget(T widget)
        {
            add(widget);
            return widget;
        }
    }

    public enum Position implements Function<Bounds, Integer>
    {
        TOP(Bounds::top),
        BOTTOM(Bounds::bottom),
        X(Bounds::x),
        Y(Bounds::y),
        WIDTH(Bounds::height),
        HEIGHT(Bounds::height),
        LEFT(Bounds::left),
        RIGHT(Bounds::right);

        private final Function<Bounds, Integer> function;

        Position(Function<Bounds, Integer> function)
        {
            this.function = function;
        }

        @Override
        public Integer apply(Bounds bounds)
        {
            return function.apply(bounds);
        }
    }
}
