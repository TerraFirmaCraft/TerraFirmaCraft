/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Objects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.dries007.tfc.common.items.ChestBlockItem;

public class ChestItemRenderer extends BlockEntityWithoutLevelRenderer
{
    private final BlockEntity chest;
    private final BlockEntityRenderDispatcher dispatch;

    public ChestItemRenderer(ChestBlockItem item)
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

        final Block block = item.getBlock();

        chest = Objects.requireNonNull(((EntityBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState()));
        dispatch = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transforms, PoseStack poseStack, MultiBufferSource source, int packedLight, int packedOverlay)
    {
        dispatch.renderItem(chest, poseStack, source, packedLight, packedOverlay);
    }
}
