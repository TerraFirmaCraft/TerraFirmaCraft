/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class PowderkegBlockEntityRenderer implements BlockEntityRenderer<PowderkegBlockEntity>
{
    private static final ResourceLocation GUNPOWDERTEXTURE = Helpers.identifier("block/powder/gunpowder");

    @Override
    public void render(PowderkegBlockEntity powderkeg, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final BlockState state = powderkeg.getBlockState();

        if (state.getValue(PowderkegBlock.SEALED))
        {
            return;
        }

        final PowderkegBlockEntity.PowderkegInventory inventory = powderkeg.getInventory();

        int count = 0;
        int maxCount = 0;

        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            count += stack.getCount();
            //separate in case of smaller fuel
            maxCount += stack.isEmpty() ? 64 : stack.getMaxStackSize();
        }
        if (count > 0)
        {
            final float y = Mth.clampedMap(count, 0, maxCount, 2f/16f, 14f/16f);

            RenderHelpers.renderTexturedFace(poseStack, buffer, 0xFFFFFF, 2f / 16f, 2f / 16f, 14f / 16f, 14f / 16, y, combinedOverlay, combinedLight, GUNPOWDERTEXTURE);
        }
    }
}
