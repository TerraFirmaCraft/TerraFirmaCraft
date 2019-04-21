/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.client.FluidSpriteCache;
import net.dries007.tfc.objects.container.ContainerBarrel;

public class GuiBarrel extends GuiContainerTFC
{
    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/barrel.png");
    private final String translationKey;

    public GuiBarrel(Container container, InventoryPlayer playerInv, String translationKey)
    {
        super(container, playerInv, RESOURCE_LOCATION);

        this.translationKey = translationKey;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int relX = mouseX - guiLeft;
        int relY = mouseY - guiTop;

        if (relX >= 7 && relY >= 19 && relX < 25 && relY < 71)
        {
            IFluidHandler tank = ((ContainerBarrel) inventorySlots).getBarrelTank();

            if (tank != null)
            {
                FluidStack fluid = tank.getTankProperties()[0].getContents();
                List<String> tooltip = new ArrayList<>();

                if (fluid == null || fluid.amount == 0)
                {
                    tooltip.add(I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_empty"));
                }
                else
                {
                    tooltip.add(fluid.getLocalizedName());
                    tooltip.add(TextFormatting.GRAY.toString() + I18n.format(TFCConstants.MOD_ID + ".tooltip.barrel_fluid_amount", fluid.amount));
                }

                this.drawHoveringText(tooltip, mouseX, mouseY, fontRenderer);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        ContainerBarrel container = (ContainerBarrel) inventorySlots;
        IFluidHandler tank = container.getBarrelTank();

        if (tank != null)
        {
            IFluidTankProperties t = tank.getTankProperties()[0];
            FluidStack fs = t.getContents();

            if (fs != null)
            {
                int fillHeightPixels = (int) (50 * fs.amount / (float) t.getCapacity());

                if (fillHeightPixels > 0)
                {
                    Fluid fluid = fs.getFluid();
                    TextureAtlasSprite sprite = FluidSpriteCache.getSprite(fluid);

                    int positionX = guiLeft + 8;
                    int positionY = guiTop + 54;

                    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    BufferBuilder buffer = Tessellator.getInstance().getBuffer();

                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                    int color = fluid.getColor();

                    float r = ((color >> 16) & 0xFF) / 255f;
                    float g = ((color >> 8) & 0xFF) / 255f;
                    float b = (color & 0xFF) / 255f;
                    float a = ((color >> 24) & 0xFF) / 255f;

                    GlStateManager.color(r, g, b, a);

                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

                    while (fillHeightPixels > 15)
                    {
                        buffer.pos(positionX, positionY, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
                        buffer.pos(positionX, positionY + 16, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
                        buffer.pos(positionX + 16, positionY + 16, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
                        buffer.pos(positionX + 16, positionY, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();

                        fillHeightPixels -= 16;
                        positionY -= 16;
                    }

                    if (fillHeightPixels > 0)
                    {
                        int blank = 16 - fillHeightPixels;
                        positionY += blank;
                        buffer.pos(positionX, positionY, 0).tex(sprite.getMinU(), sprite.getInterpolatedV(blank)).endVertex();
                        buffer.pos(positionX, positionY + fillHeightPixels, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
                        buffer.pos(positionX + 16, positionY + fillHeightPixels, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
                        buffer.pos(positionX + 16, positionY, 0).tex(sprite.getMaxU(), sprite.getInterpolatedV(blank)).endVertex();
                    }

                    Tessellator.getInstance().draw();

                    Minecraft.getMinecraft().renderEngine.bindTexture(RESOURCE_LOCATION);
                    GlStateManager.color(1, 1, 1, 1);
                }
            }
        }

        drawTexturedModalRect(guiLeft + 7, guiTop + 19, 176, 0, 18, 52);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = I18n.format(translationKey + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);

        ContainerBarrel container = (ContainerBarrel) inventorySlots;

        if (container.isBarrelSealed())
        {
            IItemHandler handler = container.getBarrelInventory();

            if (handler != null)
            {
                GL11.glDisable(GL11.GL_DEPTH_TEST);

                for (int slotId = 0; slotId < handler.getSlots(); slotId++)
                {
                    drawSlotOverlay(container.getSlot(slotId));
                }

                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    private void drawSlotOverlay(Slot slot)
    {
        int xPos = slot.xPos - 1;
        int yPos = slot.yPos - 1;

        this.drawGradientRect(xPos, yPos, xPos + 18, yPos + 18, 0x75FFFFFF, 0x75FFFFFF);
    }
}
