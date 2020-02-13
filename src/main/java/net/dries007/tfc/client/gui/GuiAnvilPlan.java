/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.client.button.GuiButtonAnvilPlanIcon;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.dries007.tfc.network.PacketGuiButton;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.NBTBuilder;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.te.TEAnvilTFC.SLOT_INPUT_1;

@SideOnly(Side.CLIENT)
public class GuiAnvilPlan extends GuiContainerTE<TEAnvilTFC>
{
    public static final ResourceLocation PLAN_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/anvil_plan.png");

    private final ItemStack inputStack;
    private int page;
    private GuiButton buttonLeft, buttonRight;

    public GuiAnvilPlan(Container container, InventoryPlayer playerInv, TEAnvilTFC tile)
    {
        super(container, playerInv, tile, PLAN_BACKGROUND);

        IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        this.inputStack = cap == null ? ItemStack.EMPTY : cap.getStackInSlot(SLOT_INPUT_1);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        page = 0;
        updatePage();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button instanceof GuiButtonAnvilPlanIcon)
        {
            // This fires when you select a plan in the Plan GUI
            TerraFirmaCraft.getLog().info("Pressed the plan button");
            ResourceLocation recipeName = ((GuiButtonAnvilPlanIcon) button).getRecipeName();
            TerraFirmaCraft.getNetwork().sendToServer(new PacketGuiButton(button.id, new NBTBuilder().setString("recipe", recipeName.toString()).build()));
        }
        else if (button == buttonLeft)
        {
            page--;
            updatePage();
        }
        else if (button == buttonRight)
        {
            page++;
            updatePage();
        }
        super.actionPerformed(button);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        // Button Tooltips
        for (GuiButton button : buttonList)
        {
            if (button instanceof IButtonTooltip && button.isMouseOver())
            {
                IButtonTooltip tooltip = (IButtonTooltip) button;
                if (tooltip.hasTooltip())
                {
                    drawHoveringText(I18n.format(tooltip.getTooltip()), mouseX, mouseY);
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = I18n.format("tfc.tooltip.anvil_plan") + ": " + I18n.format(inputStack.getTranslationKey() + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    private void updatePage()
    {
        buttonList.clear();
        int buttonID = -1;
        List<AnvilRecipe> recipeList = AnvilRecipe.getAllFor(inputStack);
        for (int i = page * 18; i < (page + 1) * 18 && i < recipeList.size(); i++)
        {
            int posX = 7 + (i % 9) * 18;
            int posY = 25 + ((i % 18) / 9) * 18;
            addButton(new GuiButtonAnvilPlanIcon(recipeList.get(i), ++buttonID, guiLeft + posX, guiTop + posY));
        }
        buttonLeft = addButton(new GuiButton(++buttonID, guiLeft + 7, guiTop + 65, 14, 14, "")
        {
            @Override
            public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
            {
                if (this.visible)
                {
                    mc.getTextureManager().bindTexture(PLAN_BACKGROUND);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                    int i = this.getHoverState(this.hovered);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    this.drawTexturedModalRect(this.x, this.y, 176, 18 + i * 14, this.width, this.height);
                    this.mouseDragged(mc, mouseX, mouseY);
                }
            }
        });

        buttonRight = addButton(new GuiButton(++buttonID, guiLeft + 154, guiTop + 65, 14, 14, "")
        {
            @Override
            public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
            {
                if (this.visible)
                {
                    mc.getTextureManager().bindTexture(PLAN_BACKGROUND);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                    int i = this.getHoverState(this.hovered);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    this.drawTexturedModalRect(this.x, this.y, 190, 18 + i * 14, this.width, this.height);
                    this.mouseDragged(mc, mouseX, mouseY);
                }
            }
        });

        if (recipeList.size() <= 18)
        {
            buttonLeft.visible = false;
            buttonRight.visible = false;
        }
        else
        {
            if (page <= 0)
            {
                buttonLeft.enabled = false;
            }
            if ((page + 1) * 18 >= recipeList.size())
            {
                buttonRight.enabled = false;
            }
        }
    }
}
