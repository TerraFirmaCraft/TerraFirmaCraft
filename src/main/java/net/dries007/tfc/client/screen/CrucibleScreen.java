/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.container.CrucibleContainer;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.PourFasterPacket;
import net.dries007.tfc.util.FluidAlloy;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;

public class CrucibleScreen extends BlockEntityScreen<CrucibleBlockEntity, CrucibleContainer>
{
    private static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/crucible.png");
    private static final int MAX_ELEMENTS = 3;

    private int scrollPos;
    private boolean scrollPress;
    private int pourFasterDecayTicks = 0;

    public CrucibleScreen(CrucibleContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);

        inventoryLabelY += 55;
        imageHeight += 55;

        scrollPos = 0;
        scrollPress = false;
    }

    @Override
    protected void containerTick()
    {
        if (pourFasterDecayTicks <= 0 && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT))
        {
            if (hoveredSlot != null)
            {
                final IMold mold = IMold.get(hoveredSlot.getItem());
                if (mold != null)
                {
                    PacketDistributor.sendToServer(new PourFasterPacket(blockEntity.getBlockPos(), hoveredSlot.index));
                    pourFasterDecayTicks = 10;
                }
            }
        }
        else
        {
            pourFasterDecayTicks--;
        }
        super.containerTick();
    }

    @Override
    protected void renderLabels(GuiGraphics stack, int mouseX, int mouseY)
    {
        // No-op - this screen basically doesn't have room for the inventory labels... how sad
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseX >= leftPos + 154 && mouseX <= leftPos + 165 && mouseY >= topPos + 11 + scrollPos && mouseY <= topPos + 26 + scrollPos)
        {
            scrollPress = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (scrollPress)
        {
            scrollPos = Math.min(Math.max((int) mouseY - topPos - 18, 0), 49);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (scrollPress && button == 0)
        {
            scrollPress = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        // Draw the temperature indicator
        int temperature = Heat.scaleTemperatureForGui(blockEntity.getTemperature());
        if (temperature > 0)
        {
            graphics.blit(texture, leftPos + 7, topPos + 131 - Math.min(temperature, 51), 176, 0, 15, 5);
        }

        // Draw the scroll bar
        graphics.blit(texture, leftPos + 154, topPos + 11 + scrollPos, 176, 7, 12, 15);

        // Draw the fluid + detailed content
        final FluidAlloy alloy = blockEntity.getAlloy();
        final FluidStack alloyResult = blockEntity.getAlloyResult();
        if (alloy.getAmount() > 0)
        {
            final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(alloyResult);
            final int fillHeight = (int) Math.ceil((float) 31 * alloy.getAmount() / blockEntity.containerInfo().fluidCapacity());

            RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 97, topPos + 124 - fillHeight, 36, fillHeight, 16, 16);

            resetToBackgroundSprite();

            // Draw Title:
            final Component resultText = alloyResult.getFluidType().getDescription().copy().withStyle(ChatFormatting.UNDERLINE);
            graphics.drawString(font, resultText, leftPos + 10, topPos + 11, 0x000000, false);

            int startElement = Math.max(0, (int) Math.floor(((alloy.getContent().size() - MAX_ELEMENTS) / 49D) * (scrollPos + 1)));

            // Draw Components
            int yPos = topPos + 22;
            int index = -1; // So the first +1 = 0
            for (Object2DoubleMap.Entry<Fluid> entry : alloy.getContent().object2DoubleEntrySet())
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
                //   XXX units (YY.Y%)
                // Metal 2 name:
                //   ZZZ units (WW.W%)

                final String metalName = font.plainSubstrByWidth(entry.getKey().getFluidType().getDescription().getString(), 141) + ":";
                // %s units (%s %)
                final MutableComponent content = Component.translatable("tfc.tooltip.crucible_content_line", Tooltips.fluidUnits(entry.getDoubleValue()), String.format("%2.1f", Math.round(1000 * entry.getDoubleValue() / alloy.getAmount()) / 10f));

                graphics.drawString(font, metalName, leftPos + 10, yPos, 0x404040, false);
                graphics.drawString(font, content, leftPos + 10, yPos + 9, 0x404040, false);
                yPos += 18;
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 7, topPos + 131 - 51, 15, 51))
        {
            final var text = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(blockEntity.getTemperature());
            if (text != null)
            {
                graphics.renderTooltip(font, text, mouseX, mouseY);
            }
        }
    }
}
