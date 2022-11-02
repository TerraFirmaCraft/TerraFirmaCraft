/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.blockentities.TFCTrappedChestBlockEntity;
import net.dries007.tfc.common.blocks.wood.TFCChestBlock;
import net.dries007.tfc.util.Helpers;

public class TFCChestBlockEntityRenderer extends ChestRenderer<TFCChestBlockEntity>
{
    private static String getFolder(BlockEntity blockEntity, ChestType type)
    {
        final String prefix = blockEntity instanceof TFCTrappedChestBlockEntity ? "trapped" : "normal";
        return chooseForType(type, prefix, prefix + "_left", prefix + "_right");
    }

    private static String chooseForType(ChestType type, String single, String left, String right)
    {
        return switch (type)
            {
                case LEFT -> left;
                case RIGHT -> right;
                case SINGLE -> single;
            };
    }

    private String wood;

    public TFCChestBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
        wood = "oak";
    }

    @Override
    public void render(TFCChestBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (be.getBlockState().getBlock() instanceof TFCChestBlock chestBlock)
        {
            wood = chestBlock.getTextureLocation();
        }
        super.render(be, partialTicks, poseStack, buffer, combinedLight, combinedOverlay);
    }

    @Override
    protected Material getMaterial(TFCChestBlockEntity blockEntity, ChestType chestType)
    {
        return new Material(Sheets.CHEST_SHEET, Helpers.identifier("entity/chest/" + getFolder(blockEntity, chestType) + "/" + wood));
    }
}
