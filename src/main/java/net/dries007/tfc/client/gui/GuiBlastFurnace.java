package net.dries007.tfc.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TEBlastFurnace;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiBlastFurnace extends GuiContainerTE<TEBlastFurnace>
{
    private static final ResourceLocation BLAST_FURNACE_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/blast_furnace.png");

    public GuiBlastFurnace(Container container, InventoryPlayer playerInv, TEBlastFurnace tile)
    {
        super(container, playerInv, tile, BLAST_FURNACE_BACKGROUND);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawSimpleBackground();

        // todo: ore + charcoal indicators
        // todo: temperature indicator
    }
}
