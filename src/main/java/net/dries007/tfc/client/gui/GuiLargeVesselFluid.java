/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.BarrelRecipe;
import net.dries007.tfc.client.FluidSpriteCache;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.button.GuiButtonLargeVesselModeTab;
import net.dries007.tfc.client.button.GuiButtonLargeVesselSeal;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.dries007.tfc.network.PacketGuiButton;
import net.dries007.tfc.network.PacketSwitchLargeVesselModeTab;
import net.dries007.tfc.objects.container.ContainerLargeVesselFluid;
import net.dries007.tfc.objects.te.TELargeVessel;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class GuiLargeVesselFluid extends GuiContainerTE<TELargeVessel>
{
    public static final ResourceLocation LARGE_VESSEL_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/large_vessel.png");
    private final String translationKey;

    public GuiLargeVesselFluid(Container container, InventoryPlayer playerInv, TELargeVessel tile, String translationKey)
    {
        super(container, playerInv, tile, LARGE_VESSEL_BACKGROUND);

        this.translationKey = translationKey;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        addButton(new GuiButtonLargeVesselSeal(tile, 0, guiTop, guiLeft));

        addButton(new GuiButtonLargeVesselModeTab(TFCGuiHandler.Type.LARGE_VESSEL_FLUID, tile, 1, guiTop, guiLeft, false));
        addButton(new GuiButtonLargeVesselModeTab(TFCGuiHandler.Type.LARGE_VESSEL_SOLID, tile, 2, guiTop, guiLeft, true));
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);

        int relX = mouseX - guiLeft;
        int relY = mouseY - guiTop;

        if (relX >= 7 && relY >= 19 && relX < 25 && relY < 71)
        {
            IFluidHandler tank = ((ContainerLargeVesselFluid) inventorySlots).getBarrelTank();

            if (tank != null)
            {
                FluidStack fluid = tank.getTankProperties()[0].getContents();
                List<String> tooltip = new ArrayList<>();

                if (fluid == null || fluid.amount == 0)
                {
                    tooltip.add(I18n.format(MOD_ID + ".tooltip.barrel_empty"));
                }
                else
                {
                    tooltip.add(fluid.getLocalizedName());
                    tooltip.add(TextFormatting.GRAY.toString() + I18n.format(MOD_ID + ".tooltip.barrel_fluid_amount", fluid.amount));
                }

                this.drawHoveringText(tooltip, mouseX, mouseY, fontRenderer);
            }
        }

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
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = I18n.format(translationKey + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);

        if (tile.isSealed())
        {
            // Draw over the input items, making them look unavailable
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler != null)
            {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                for (int slotId = 0; slotId < 3; slotId++)
                {
                    drawSlotOverlay(inventorySlots.getSlot(slotId));
                }
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            // Draw the text displaying both the seal date, and the recipe name
            fontRenderer.drawString(tile.getSealedDate(), 46, 73, 0x404040);

            BarrelRecipe recipe = tile.getRecipe();
            if (recipe != null)
            {
                String resultName = recipe.getResultName();
                fontRenderer.drawString(resultName, 59, 19, 0x404040);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        switch (tile.getGuiTabs())
        {
            case TELargeVessel.GUI_TAB_ALL:
                this.buttonList.get(TELargeVessel.GUI_TAB_FLUID).enabled = true;
                this.buttonList.get(TELargeVessel.GUI_TAB_SOLID).enabled = true;
                break;
            case TELargeVessel.GUI_TAB_FLUID:
                this.buttonList.get(TELargeVessel.GUI_TAB_SOLID).enabled = false;
                break;
            case TELargeVessel.GUI_TAB_SOLID:
                this.buttonList.get(TELargeVessel.GUI_TAB_FLUID).enabled = false;
                break;
        }

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        ContainerLargeVesselFluid container = (ContainerLargeVesselFluid) inventorySlots;
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

                    Minecraft.getMinecraft().renderEngine.bindTexture(LARGE_VESSEL_BACKGROUND);
                    GlStateManager.color(1, 1, 1, 1);
                }
            }
        }

        drawTexturedModalRect(guiLeft + 7, guiTop + 19, 176, 0, 18, 52);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button instanceof GuiButtonLargeVesselSeal)
        {
            TerraFirmaCraft.getNetwork().sendToServer(new PacketGuiButton(button.id));
        }
        else if (button instanceof GuiButtonLargeVesselModeTab)
        {
            GuiButtonLargeVesselModeTab tabButton = (GuiButtonLargeVesselModeTab) button;
            if (tabButton.isActive())
            {
                TerraFirmaCraft.getNetwork().sendToServer(new PacketSwitchLargeVesselModeTab(tile, tabButton.getGuiType()));
                tile.setActiveGuiTab(tabButton.getGuiType());
            }
        }
        super.actionPerformed(button);
    }

    private void drawSlotOverlay(Slot slot)
    {
        int xPos = slot.xPos - 1;
        int yPos = slot.yPos - 1;

        this.drawGradientRect(xPos, yPos, xPos + 18, yPos + 18, 0x75FFFFFF, 0x75FFFFFF);
    }
}
