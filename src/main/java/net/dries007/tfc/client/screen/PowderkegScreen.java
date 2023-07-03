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

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PowderkegSealButton;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.common.container.PowderkegContainer;
import net.dries007.tfc.util.Helpers;

public class PowderkegScreen extends BlockEntityScreen<PowderkegBlockEntity, PowderkegContainer>
{
    private static final Component SEAL = Component.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.seal_barrel");
    private static final Component UNSEAL = Component.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.unseal_barrel");

    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/powderkeg.png");

    public PowderkegScreen(PowderkegContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new PowderkegSealButton(blockEntity, getGuiLeft(), getGuiTop(), isSealed() ? UNSEAL : SEAL));
    }

    private boolean isSealed()
    {
        return blockEntity.getBlockState().getValue(PowderkegBlock.SEALED);
    }

    @Override
    protected void renderLabels(GuiGraphics poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);
        if (isSealed())
        {
            drawDisabled(poseStack, 0, PowderkegBlockEntity.SLOTS - 1);
        }
    }
}
