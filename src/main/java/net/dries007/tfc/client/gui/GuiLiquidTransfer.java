/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.util.Helpers;

@SideOnly(Side.CLIENT)
public class GuiLiquidTransfer extends GuiContainerTFC
{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/small_vessel_liquid.png");
    private final int slotIdx;

    public GuiLiquidTransfer(Container container, EntityPlayer player, String translationKey, boolean offhand)
    {
        super(container, player.inventory, BG_TEXTURE, translationKey);

        if (offhand)
            slotIdx = 40;
        else
            slotIdx = playerInv.currentItem;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        ItemStack stack = playerInv.getStackInSlot(slotIdx);
        IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (cap instanceof IMoldHandler)
        {
            Metal metal = ((IMoldHandler) cap).getMetal();
            if (metal != null)
            {
                String metalName = I18n.format(Helpers.getTypeName(metal));
                String amountName = I18n.format("tfc.tooltip.units", ((IMoldHandler) cap).getAmount());
                fontRenderer.drawString(metalName, xSize / 2 - fontRenderer.getStringWidth(metalName) / 2, 14, 0x404040);
                fontRenderer.drawString(amountName, xSize / 2 - fontRenderer.getStringWidth(amountName) / 2, 23, 0x404040);
            }
        }
        fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
    }

}
