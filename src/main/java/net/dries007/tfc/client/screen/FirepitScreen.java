/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.FirepitBlockEntity;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class FirepitScreen extends BlockEntityScreen<FirepitBlockEntity, FirepitContainer>
{
    private static final ResourceLocation FIREPIT = Helpers.identifier("textures/gui/fire_pit.png");

    public FirepitScreen(FirepitContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, FIREPIT);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        int temp = Heat.scaleTemperatureForGui(blockEntity.getTemperature());
        if (temp > 0)
        {
            graphics.blit(texture, leftPos + 30, topPos + 76 - Math.min(51, temp), 176, 0, 15, 5);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 30, topPos + 76 - 51, 15, 51))
        {
            final var text = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(blockEntity.getTemperature());
            if (text != null)
            {
                graphics.renderTooltip(font, text, mouseX, mouseY);
            }
        }
    }
}
