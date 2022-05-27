/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.container.BlastFurnaceContainer;
import net.dries007.tfc.util.Helpers;

public class BlastFurnaceScreen extends BlockEntityScreen<BlastFurnaceBlockEntity, BlastFurnaceContainer>
{
    private static final ResourceLocation BLAST_FURNACE = Helpers.identifier("textures/gui/blast_furnace.png");

    public BlastFurnaceScreen(BlastFurnaceContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BLAST_FURNACE);

        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderTooltip(poseStack, mouseX, mouseY);
    }
}
