/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.blockentities.LargeVesselBlockEntity;
import net.dries007.tfc.common.blocks.LargeVesselBlock;
import net.dries007.tfc.common.container.LargeVesselContainer;
import net.dries007.tfc.util.Helpers;

public class LargeVesselScreen extends BlockEntityScreen<LargeVesselBlockEntity, LargeVesselContainer>
{
    public LargeVesselScreen(LargeVesselContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, Helpers.identifier("textures/gui/large_vessel.png"));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);
        if (isSealed())
        {
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inventory -> {
                // draw disabled texture over the slots
                menu.slots.stream().filter(slot -> slot.index < LargeVesselBlockEntity.SLOTS).forEach(slot -> fillGradient(poseStack, slot.x, slot.y, slot.x + 16, slot.y + 16, 0x75FFFFFF, 0x75FFFFFF));
            });
        }
    }

    private boolean isSealed()
    {
        return blockEntity.getBlockState().getValue(LargeVesselBlock.SEALED);
    }
}
