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
import net.dries007.tfc.client.screen.LargeVesselScreen;
import net.dries007.tfc.common.blockentities.LargeVesselBlockEntity;
import net.dries007.tfc.common.blocks.LargeVesselBlock;
import net.dries007.tfc.network.ScreenButtonPacket;

public class VesselSealButton extends Button
{
    private final LargeVesselBlockEntity vessel;

    public VesselSealButton(LargeVesselBlockEntity barrel, int guiLeft, int guiTop, Component tooltip)
    {
        super(guiLeft + 123, guiTop + 35, 20, 20, tooltip, b -> {}, RenderHelpers.NARRATION);
        setTooltip(Tooltip.create(tooltip));
        this.vessel = barrel;
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
        final int v = vessel.getBlockState().getValue(LargeVesselBlock.SEALED) ? 0 : 20;
        graphics.blit(LargeVesselScreen.BACKGROUND, getX(), getY(), 236, v, 20, 20, 256, 256);
    }
}
