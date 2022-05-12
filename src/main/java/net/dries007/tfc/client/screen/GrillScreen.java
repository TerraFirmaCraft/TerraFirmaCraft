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
import net.dries007.tfc.common.blockentities.GrillBlockEntity;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.container.GrillContainer;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GrillScreen extends BlockEntityScreen<GrillBlockEntity, GrillContainer>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit_grill.png");

    public GrillScreen(GrillContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);
        int temp = (int) (51 * blockEntity.getTemperature() / Heat.maxVisibleTemperature());
        if (temp > 0)
        {
            blit(poseStack, leftPos + 30, topPos + 76 - Math.min(51, temp), 176, 0, 15, 5);
        }
    }
}
