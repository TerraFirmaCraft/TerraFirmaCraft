package net.dries007.tfc.compat.emi.recipe;

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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiGlassworkingRecipe extends GenericRecipe<GlassworkingRecipe>
{
    private static final int OPERATIONS_PER_PAGE = 2;
    private int currentPage = 0;
    private final int maxPages;
    private final int operationCount;
    private GlassworkingPageLabel label;


    public EmiGlassworkingRecipe(ResourceLocation id, GlassworkingRecipe recipe)
    {
        super(EmiIntegration.GLASSWORKING, id, recipe, 175, 110);
        maxPages = 1;
        inputs.add(EmiIngredient.of(recipe.batchItem()));

        operationCount = recipe.operations().size();

        ItemStack result = recipe.getResultItem(null);
        if (result.getItem() instanceof BlockItem bi && bi.getBlock() instanceof PouredGlassBlock block)
        {
            result = block.getDrop().getDefaultInstance();
        }
        outputs.add(EmiStack.of(result));
        label = new GlassworkingPageLabel(getLabelText(), 125, 15, 0xffffff, true);
        label.horizontalAlign(TextWidget.Alignment.CENTER);
        label.verticalAlign(TextWidget.Alignment.CENTER);
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.getFirst(), 6, 6);
        widgets.addSlot(outputs.getFirst(), 56, 6).recipeContext(this);
        widgets.addFillingArrow(28, 7, 3000);
        widgets.add(new GlassworkingStepWidget(EmiStack.of(TFCItems.ALFALFA), 30, 40));
        widgets.add(new GlassworkingPageButton(label.getBounds().right() + 6, 9, 12, 12, 12, 0, this::hasNextPage, this::nextStepPage));
        widgets.add(new GlassworkingPageButton(label.getBounds().left() - 18, 9, 12, 12, 0, 0, this::hasPrevPage, this::prevStepPage));
        widgets.add(label);
    }

    private MutableComponent getLabelText()
    {
        return Component.literal(Math.min(operationCount, (currentPage + 1) * OPERATIONS_PER_PAGE) + "/" + operationCount);
    }

    private void prevStepPage(double x, double y, int button)
    {
        if (hasPrevPage())
        {
            currentPage -= 1;
            label.updateText(getLabelText());
            playClick();
        }
    }

    private void nextStepPage(double x, double y, int button)
    {
        if (hasNextPage())
        {
            currentPage += 1;
            label.updateText(getLabelText());
            playClick();
        }
    }

    private boolean hasNextPage()
    {
        int viewedOperations = (currentPage + 1) * OPERATIONS_PER_PAGE;
        return viewedOperations < operationCount;
    }

    private boolean hasPrevPage()
    {
        return currentPage > 0;
    }

    private void playClick()
    {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    private static class GlassworkingStepWidget extends SlotWidget
    {

        public GlassworkingStepWidget(EmiIngredient stack, int x, int y)
        {
            super(stack, x, y);
        }
    }

    private static class GlassworkingPageButton extends ButtonWidget
    {

        public GlassworkingPageButton(int x, int y, int width, int height, int u, int v, BooleanSupplier isActive, ClickAction action)
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

    private static class GlassworkingPageLabel extends Widget
    {
        private final int x;
        private final int y;
        private final int color;
        private final boolean shadow;
        private TextWidget widget;
        private TextWidget.Alignment horizontalAlignment = TextWidget.Alignment.START;
        private TextWidget.Alignment verticalAlignment = TextWidget.Alignment.START;

        public GlassworkingPageLabel(Component text, int x, int y, int color, boolean shadow)
        {
            this.x = x;
            this.y = y;
            this.color = color;
            this.shadow = shadow;
            updateText(text);
        }

        public void updateText(Component text)
        {
            widget = new TextWidget(text.getVisualOrderText(), x, y, color, shadow);
            widget.horizontalAlign(horizontalAlignment);
            widget.verticalAlign(verticalAlignment);
        }

        public void horizontalAlign(TextWidget.Alignment alignment)
        {
            this.horizontalAlignment = alignment;
            widget.horizontalAlign(alignment);
        }

        public void verticalAlign(TextWidget.Alignment alignment)
        {
            this.verticalAlignment = alignment;
            widget.verticalAlign(alignment);
        }

        @Override
        public Bounds getBounds()
        {
            return widget.getBounds();
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            widget.render(draw, mouseX, mouseY, delta);
        }
    }
}
