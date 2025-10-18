/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import java.util.function.BooleanSupplier;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.ButtonWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiGlassworkingRecipe extends BasicRecipe<GlassworkingRecipe>
{
    private static final int OPERATIONS_PER_PAGE = 6;
    private final GlassworkingStepWidget[] stepWidgets;
    private final List<GlassOperation> operations;
    private int currentPage = 0;
    private @Nullable PageControlsWidget pageControls;


    public EmiGlassworkingRecipe(ResourceLocation id, GlassworkingRecipe recipe)
    {
        super(EmiIntegration.GLASSWORKING, id, 175, 30);
        inputs.add(EmiIngredient.of(recipe.batchItem()));
        operations = recipe.operations();
        for (GlassOperation operation : operations)
        {
            inputs.add(EmiIngredient.of(operation.getItems().stream().map(Holder::value).map(EmiStack::of).toList()));
        }

        ItemStack result = recipe.getResultItem(null);
        if (result.getItem() instanceof BlockItem bi && bi.getBlock() instanceof PouredGlassBlock block)
        {
            result = block.getDrop().getDefaultInstance();
        }
        outputs.add(EmiStack.of(result));

        // These widgets MUST be created here
        // addWidgets can be called while viewing this recipe by looking at a pinned recipe,
        // which would replace our widgets with new widgets that do not respond to input
        int widgetCount = Math.min(operations.size(), OPERATIONS_PER_PAGE);
        stepWidgets = new GlassworkingStepWidget[widgetCount];
        for (int i = 0; i < widgetCount; i++)
        {
            stepWidgets[i] = new GlassworkingStepWidget(6, 30 + (i * 20));
        }
        updateSteps();
        if (operations.size() > OPERATIONS_PER_PAGE)
        {
            pageControls = new PageControlsWidget(getLabelText(), 125, 20, this::hasPrevPage, this::prevStepPage, this::hasNextPage, this::nextStepPage);
        }
    }

    @Override
    public int getDisplayHeight()
    {
        return stepWidgets[stepWidgets.length - 1].getBounds().bottom() + 4;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.getFirst(), 6, 6);
        widgets.addSlot(outputs.getFirst(), 56, 6).recipeContext(this);
        widgets.addFillingArrow(28, 7, 3000);

        if (pageControls != null)
        {
            widgets.add(pageControls);
            TextWidget label = widgets.addText(Component.translatable("tfc.tooltip.glass.title"), 125, 5, 0xffffff, true);
            label.horizontalAlign(TextWidget.Alignment.CENTER).verticalAlign(TextWidget.Alignment.CENTER);
        }
        for (GlassworkingStepWidget step : stepWidgets)
        {
            widgets.add(step);
        }
    }

    private MutableComponent getLabelText()
    {
        int operationCount = operations.size();
        int currentSteps = Math.min(operationCount, (currentPage + 1) * OPERATIONS_PER_PAGE);
        return Component.translatable("tfc.tooltip.glass.step_count", currentSteps, operationCount);
    }

    private void prevStepPage(double x, double y, int button)
    {
        if (hasPrevPage() && pageControls != null)
        {
            currentPage -= 1;
            pageControls.updateText(getLabelText());
            updateSteps();
            playClick();
        }
    }

    private void nextStepPage(double x, double y, int button)
    {
        if (hasNextPage() && pageControls != null)
        {
            currentPage += 1;
            pageControls.updateText(getLabelText());
            updateSteps();
            playClick();
        }
    }

    private boolean hasNextPage()
    {
        int viewedOperations = (currentPage + 1) * OPERATIONS_PER_PAGE;
        return viewedOperations < operations.size();
    }

    private boolean hasPrevPage()
    {
        return currentPage > 0;
    }

    private void updateSteps()
    {
        int startingStep = currentPage * OPERATIONS_PER_PAGE;
        int operationCount = operations.size();
        for (int i = 0; i < stepWidgets.length; i++)
        {
            GlassworkingStepWidget widget = stepWidgets[i];
            int index = startingStep + i;
            if (index < operationCount)
            {
                widget.setOperation(operations.get(index), index);
            }
            else
            {
                widget.setOperation(null, index);
            }
        }
    }

    private void playClick()
    {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    private static class GlassworkingStepWidget extends SlotWidget
    {
        private EmiIngredient displayStack;
        private @Nullable TextWidget label;

        public GlassworkingStepWidget(int x, int y)
        {
            super(null, x, y);
            setOperation(null, 0);
        }

        @Override
        public EmiIngredient getStack()
        {
            return this.displayStack;
        }

        public void setOperation(@Nullable GlassOperation operation, int index)
        {
            if (operation == null)
            {
                label = null;
                displayStack = EmiStack.of(ItemStack.EMPTY);
            }
            else
            {
                Component labelText = Component.literal((index + 1) + ". ").append(Component.translatable(operation.getTranslationId()));
                Bounds bounds = getBounds();
                int yOff = TextWidget.Alignment.CENTER.offset(bounds.height());
                label = new TextWidget(labelText.getVisualOrderText(), bounds.right() + 3, bounds.bottom() + yOff, 0xffffff, true);
                label.verticalAlign(TextWidget.Alignment.CENTER);
                displayStack = EmiIngredient.of(operation.getItems().stream().map(Holder::value).map(EmiStack::of).toList());
            }
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            super.render(draw, mouseX, mouseY, delta);
            if (label != null)
            {
                label.render(draw, mouseX, mouseY, delta);
            }
        }
    }

    /**
     * Buttons by default make a clicking noise even when disabled
     */
    private static class SilentButtonWidget extends ButtonWidget
    {
        public SilentButtonWidget(int x, int y, int width, int height, int u, int v, BooleanSupplier isActive, ClickAction action)
        {
            super(x, y, width, height, u, v, isActive, action);
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int button)
        {
            this.action.click(mouseX, mouseY, button);
            return true;
        }
    }

    /**
     * TextWidget wrapper that allows changing the text rendered, needed for when changing a page
     */
    private static class PageControlsWidget extends Widget
    {
        private static final int BUTTON_SIZE = 12;
        private final int BUTTON_MARGIN = 6;
        private final int x;
        private final int y;
        private TextWidget text;
        private @Nullable SilentButtonWidget prevButton;
        private @Nullable SilentButtonWidget nextButton;
        private final BooleanSupplier checkPrev;
        private final ButtonWidget.ClickAction onPrev;
        private final BooleanSupplier checkNext;
        private final ButtonWidget.ClickAction onNext;

        public PageControlsWidget(Component text, int x, int y, BooleanSupplier checkPrev, ButtonWidget.ClickAction onPrev, BooleanSupplier checkNext, ButtonWidget.ClickAction onNext)
        {
            this.x = x;
            this.y = y;
            this.checkPrev = checkPrev;
            this.checkNext = checkNext;
            this.onPrev = onPrev;
            this.onNext = onNext;
            updateText(text);
        }

        public void updateText(Component text)
        {
            Bounds oldBounds = this.text != null ? this.text.getBounds() : null;
            this.text = new TextWidget(text.getVisualOrderText(), x, y, 0xffffff, true);
            this.text.horizontalAlign(TextWidget.Alignment.CENTER);
            this.text.verticalAlign(TextWidget.Alignment.CENTER);
            Bounds textBounds = this.text.getBounds();

            if (!textBounds.equals(oldBounds))
            {
                int left = textBounds.left() - BUTTON_SIZE - BUTTON_MARGIN;
                int right = textBounds.right() + BUTTON_MARGIN;
                int yPos = y - BUTTON_SIZE / 2;
                prevButton = new SilentButtonWidget(left, yPos, BUTTON_SIZE, BUTTON_SIZE, 0, 0, checkPrev, onPrev);
                nextButton = new SilentButtonWidget(right, yPos, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, 0, checkNext, onNext);
            }
        }

        @Override
        public Bounds getBounds()
        {
            if (prevButton == null || nextButton == null)
            {
                return text.getBounds();
            }
            Bounds prevBounds = prevButton.getBounds();
            Bounds nextBounds = nextButton.getBounds();
            Bounds textBounds = text.getBounds();
            int height = Math.max(BUTTON_SIZE, textBounds.height());
            int width = prevBounds.width() + BUTTON_MARGIN * 2 + textBounds.width() + nextBounds.width();
            return new Bounds(prevBounds.left(), prevBounds.top(), width, height);
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            text.render(draw, mouseX, mouseY, delta);
            if (prevButton != null)
            {
                prevButton.render(draw, mouseX, mouseY, delta);
            }
            if (nextButton != null)
            {
                nextButton.render(draw, mouseX, mouseY, delta);
            }
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int button)
        {
            if (text.getBounds().contains(mouseX, mouseY))
            {
                return text.mouseClicked(mouseX, mouseY, button);
            }
            if (prevButton != null && prevButton.getBounds().contains(mouseX, mouseY))
            {
                return prevButton.mouseClicked(mouseX, mouseY, button);
            }
            if (nextButton != null && nextButton.getBounds().contains(mouseX, mouseY))
            {
                return nextButton.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }
    }
}
