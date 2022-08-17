/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;

public class BlockEntityScreen<T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> extends TFCContainerScreen<C>
{
    protected final T blockEntity;

    public BlockEntityScreen(C container, Inventory playerInventory, Component name, ResourceLocation texture)
    {
        super(container, playerInventory, name, texture);
        this.blockEntity = container.getBlockEntity();
    }

    public void resetToBackgroundSprite()
    {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, texture);
    }

    public void drawDisabled(PoseStack poseStack, int start, int end)
    {
        blockEntity.getCapability(Capabilities.ITEM).ifPresent(inventory -> {
            // draw disabled texture over the slots
            menu.slots.stream().filter(slot -> slot.index <= end && slot.index >= start).forEach(slot -> fillGradient(poseStack, slot.x, slot.y, slot.x + 16, slot.y + 16, 0x75FFFFFF, 0x75FFFFFF));
        });
    }
}
