/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.BarrelScreen;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.network.ScreenButtonPacket;

public class BarrelSealButton extends Button
{
    private final BarrelBlockEntity barrel;

    public BarrelSealButton(BarrelBlockEntity barrel, int guiLeft, int guiTop, Component tooltip)
    {
        super(guiLeft + 123, guiTop + 35, 20, 20, tooltip, b -> {}, RenderHelpers.NARRATION);

        setTooltip(Tooltip.create(tooltip));
        this.barrel = barrel;
    }

    @Override
    public void onPress()
    {
        PacketDistributor.sendToServer(new ScreenButtonPacket(0));
        playDownSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        final int v = barrel.getBlockState().getValue(BarrelBlock.SEALED) ? 0 : 20;
        graphics.blit(BarrelScreen.BACKGROUND, getX(), getY(), 236, v, 20, 20, 256, 256);
    }
}
