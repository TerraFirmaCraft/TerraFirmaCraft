/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.io.IOException;

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
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.client.button.GuiButtonAnvilPlanIcon;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.dries007.tfc.network.PacketGuiButton;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.NBTBuilder;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.objects.te.TEAnvilTFC.SLOT_INPUT_1;

@SideOnly(Side.CLIENT)
public class GuiAnvilPlan extends GuiContainerTE<TEAnvilTFC>
{
    public static final ResourceLocation PLAN_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/anvil_plan.png");

    private final ItemStack inputStack;

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

        int buttonID = -1;
        int posX = 7;
        int posY = 25;
        for (AnvilRecipe recipe : AnvilRecipe.getAllFor(inputStack))
        {
            addButton(new GuiButtonAnvilPlanIcon(recipe, ++buttonID, guiLeft + posX, guiTop + posY));
            posX += 18;
            if (posX == 169)
            {
                posY += 18;
                posX = 7;
            }
        }
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
        super.actionPerformed(button);
    }
}
