/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;

import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.FluidSpriteCache;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.te.TECrucible;
import net.dries007.tfc.util.Alloy;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GuiCrucible extends GuiContainerTE<TECrucible>
{
    private static final ResourceLocation CRUCIBLE_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/crucible.png");
    private static final int MAX_ELEMENTS = 3;
    private int scrollPos;
    private boolean scrollPress;

    public GuiCrucible(Container container, InventoryPlayer playerInv, TECrucible tile)
    {
        super(container, playerInv, tile, CRUCIBLE_BACKGROUND);

        this.ySize = 221;
        scrollPos = 0;
        scrollPress = false;
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        if (tile.getAlloy().getAmount() > 0)
        {
            int startX = 97;
            int startY = 93;
            int endX = 133;
            int endY = 124;
            if (mouseX >= guiLeft + startX && mouseX < guiLeft + endX && mouseY >= guiTop + startY && mouseY < guiTop + endY)
            {
                List<String> tooltip = new ArrayList<>();
                tooltip.add(I18n.format(tile.getAlloy().getResult().getTranslationKey()));
                int amount = tile.getAlloy().getAmount();
                int maxAmount = tile.getAlloy().getMaxAmount();
                tooltip.add(I18n.format(MOD_ID + ".tooltip.crucible_units", amount, maxAmount));
                drawHoveringText(tooltip, mouseX, mouseY);
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= guiLeft + 154 && mouseX <= guiLeft + 165 && mouseY >= guiTop + 11 + scrollPos && mouseY <= guiTop + 26 + scrollPos)
        {
            scrollPress = true;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (!Mouse.isButtonDown(0))
        {
            scrollPress = false;
        }
        if (scrollPress)
        {
            scrollPos = Math.min(Math.max(mouseY - guiTop - 18, 0), 49);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // Draw the temperature indicator
        int temperature = (int) (51 * tile.getField(TECrucible.FIELD_TEMPERATURE) / Heat.maxVisibleTemperature());
        if (temperature > 0)
        {
            if (temperature > 51)
            {
                temperature = 51;
            }
            drawTexturedModalRect(guiLeft + 7, guiTop + 131 - temperature, 176, 0, 15, 5);
        }

        // Draw the scroll bar
        drawTexturedModalRect(guiLeft + 154, guiTop + 11 + scrollPos, 176, 7, 12, 15);

        // Draw the fluid + detailed content
        Alloy alloy = tile.getAlloy();
        if (alloy.getAmount() > 0)
        {
            int startX = 97;
            int startY = 93;
            int endX = 133;
            int endY = 124;

            int fillHeight = (int) Math.ceil((float) (endY - startY) * alloy.getAmount() / alloy.getMaxAmount());

            Fluid fluid = FluidsTFC.getFluidFromMetal(alloy.getResult());
            TextureAtlasSprite sprite = FluidSpriteCache.getStillSprite(fluid);

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

            int yPos = endY;
            while (fillHeight > 0)
            {
                int yPixels = Math.min(fillHeight, 16);
                int fillWidth = endX - startX;
                int xPos = endX;
                while (fillWidth > 0)
                {
                    int xPixels = Math.min(fillWidth, 16);
                    buffer.pos(guiLeft + xPos - xPixels, guiTop + yPos - yPixels, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
                    buffer.pos(guiLeft + xPos - xPixels, guiTop + yPos, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
                    buffer.pos(guiLeft + xPos, guiTop + yPos, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
                    buffer.pos(guiLeft + xPos, guiTop + yPos - yPixels, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
                    fillWidth -= 16;
                    xPos -= 16;
                }
                fillHeight -= 16;
                yPos -= 16;
            }
            Tessellator.getInstance().draw();

            Minecraft.getMinecraft().renderEngine.bindTexture(CRUCIBLE_BACKGROUND);
            GlStateManager.color(1, 1, 1, 1);

            // Draw Title:
            Metal result = tile.getAlloyResult();
            String resultText = TextFormatting.UNDERLINE + I18n.format(result.getTranslationKey());
            fontRenderer.drawString(resultText, guiLeft + 10, guiTop + 11, 0x000000);

            int startElement = Math.max(0, (int) Math.floor(((alloy.getMetals().size() - MAX_ELEMENTS) / 49D) * (scrollPos + 1)));

            // Draw Components
            yPos = guiTop + 22;
            int index = -1; // So the first +1 = 0
            for (Map.Entry<Metal, Double> entry : alloy.getMetals().entrySet())
            {
                index++;
                if (index < startElement)
                {
                    continue;
                }
                if (index > startElement - 1 + MAX_ELEMENTS)
                {
                    break;
                }
                // Draw the content, format:
                // Metal name:
                //   XXX units(YY.Y)%
                // Metal 2 name:
                //   ZZZ units(WW.W)%

                String metalName = fontRenderer.trimStringToWidth(I18n.format(entry.getKey().getTranslationKey()), 141);
                metalName += ":";
                String units;
                if (entry.getValue() >= 1)
                {
                    units = I18n.format(MOD_ID + ".tooltip.units", entry.getValue().intValue());
                }
                else
                {
                    units = I18n.format(MOD_ID + ".tooltip.crucible_less_than_one");
                }
                String content = String.format("  %s(%s%2.1f%%%s)", units, TextFormatting.DARK_GREEN, 100 * entry.getValue() / alloy.getAmount(), TextFormatting.RESET);
                fontRenderer.drawString(metalName, guiLeft + 10, yPos, 0x404040);
                fontRenderer.drawString(content, guiLeft + 10, yPos + 9, 0x404040);
                yPos += 18;
            }
        }
    }
}
