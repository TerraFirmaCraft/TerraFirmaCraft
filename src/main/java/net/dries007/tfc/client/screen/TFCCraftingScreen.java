package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dries007.tfc.common.container.TFCWorkbenchContainer;

/**
 * Copy of {@link WorkbenchContainer} because of generics
 */
public class TFCCraftingScreen extends ContainerScreen<TFCWorkbenchContainer> implements IRecipeShownListener
{
    private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookGui book = new RecipeBookGui();
    private boolean widthTooNarrow;

    public TFCCraftingScreen(TFCWorkbenchContainer container, PlayerInventory inv, ITextComponent titleIn)
    {
        super(container, inv, titleIn);
    }

    public void recipesUpdated()
    {
        book.recipesUpdated();
    }

    public RecipeBookGui getRecipeBookComponent()
    {
        return book;
    }

    protected void init()
    {
        super.init();
        widthTooNarrow = width < 379;
        if (minecraft != null)
            book.init(width, height, minecraft, widthTooNarrow, menu);
        leftPos = book.updateScreenPosition(widthTooNarrow, width, imageWidth);
        children.add(book);
        setInitialFocus(book);
        addButton(new ImageButton(leftPos + 5, height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) -> {
            book.initVisuals(widthTooNarrow);
            book.toggleVisibility();
            leftPos = book.updateScreenPosition(widthTooNarrow, width, imageWidth);
            ((ImageButton) button).setPosition(leftPos + 5, height / 2 - 49);
        }));
        titleLabelX = 29;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
        if (book.isVisible() && widthTooNarrow)
        {
            renderBg(matrixStack, partialTicks, mouseX, mouseY);
            book.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            book.render(matrixStack, mouseX, mouseY, partialTicks);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            book.renderGhostRecipe(matrixStack, leftPos, topPos, true, partialTicks);
        }

        renderTooltip(matrixStack, mouseX, mouseY);
        book.renderTooltip(matrixStack, leftPos, topPos, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (minecraft == null) return;
        minecraft.getTextureManager().bind(CRAFTING_TABLE_LOCATION);
        int i = leftPos;
        int j = (height - imageHeight) / 2;
        blit(matrixStack, i, j, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonIn)
    {
        if (book.mouseClicked(mouseX, mouseY, buttonIn))
        {
            setFocused(book);
            return true;
        }
        else
        {
            return widthTooNarrow && book.isVisible() || super.mouseClicked(mouseX, mouseY, buttonIn);
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton)
    {
        boolean flag = mouseX < (double) guiLeftIn || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + imageWidth) || mouseY >= (double) (guiTopIn + imageHeight);
        return book.hasClickedOutside(mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight, mouseButton) && flag;
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY)
    {
        return (!widthTooNarrow || !book.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    protected void slotClicked(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.slotClicked(slotIn, slotId, mouseButton, type);
        book.slotClicked(slotIn);
    }

    @Override
    public void removed()
    {
        book.removed();
        super.removed();
    }

    @Override
    public void tick()
    {
        super.tick();
        book.tick();
    }
}
