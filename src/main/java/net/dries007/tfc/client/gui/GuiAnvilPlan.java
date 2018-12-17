/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.button.GuiButtonAnvilPlanIcon;
import net.dries007.tfc.objects.te.TEAnvilTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.objects.te.TEAnvilTFC.SLOT_INPUT_1;

public class GuiAnvilPlan extends GuiContainerTE<TEAnvilTFC>
{
    private static final ResourceLocation PLAN_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/anvil_plan.png");

    private final ItemStack inputStack;

    public GuiAnvilPlan(Container container, InventoryPlayer playerInv, TEAnvilTFC tile)
    {
        super(container, playerInv, tile, PLAN_BACKGROUND);

        IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        this.inputStack = cap == null ? ItemStack.EMPTY : cap.getStackInSlot(SLOT_INPUT_1);

        xSize = 100;
        ySize = 30;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        addButton(new GuiButtonAnvilPlanIcon(0, guiLeft + 30, guiTop + 30));
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
        if (button.id == 0)
        {
            // Press a thing
            TerraFirmaCraft.getLog().info("Pressed the plan button");

        }
        super.actionPerformed(button);
    }
}
