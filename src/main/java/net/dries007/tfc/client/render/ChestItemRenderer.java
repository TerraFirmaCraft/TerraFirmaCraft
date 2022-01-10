package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.blockentities.TFCTrappedChestBlockEntity;
import net.dries007.tfc.common.blocks.wood.TFCTrappedChestBlock;

public class ChestItemRenderer extends BlockEntityWithoutLevelRenderer
{
    private final TFCChestBlockEntity be;
    private final BlockEntityRenderDispatcher dispatch;

    public ChestItemRenderer(Block block)
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        if (block instanceof TFCTrappedChestBlock)
        {
            be = new TFCTrappedChestBlockEntity(BlockPos.ZERO, block.defaultBlockState());
        }
        else
        {
            be = new TFCChestBlockEntity(BlockPos.ZERO, block.defaultBlockState());
        }
        this.dispatch = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transforms, PoseStack poseStack, MultiBufferSource source, int packedLight, int packedOverlay)
    {
        dispatch.renderItem(be, poseStack, source, packedLight, packedOverlay);
    }
}
