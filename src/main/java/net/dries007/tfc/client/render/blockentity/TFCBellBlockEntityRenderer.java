/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blocks.TFCBellBlock;

public class TFCBellBlockEntityRenderer extends BellRenderer
{
    private final Map<ResourceLocation, Material> materials = new HashMap<>();
    private final ModelPart bellBody;

    public TFCBellBlockEntityRenderer(BlockEntityRendererProvider.Context ctx)
    {
        super(ctx);
        final ModelPart part = ctx.bakeLayer(RenderHelpers.layerId("bell_body"));
        this.bellBody = part.getChild("bell_body");
    }

    @Override
    public void render(BellBlockEntity bell, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        if (bell.getBlockState().getBlock() instanceof TFCBellBlock bellBlock)
        {
            final float ticks = bell.ticks + partialTick;
            float xRot = 0.0F;
            float zRot = 0.0F;
            if (bell.shaking)
            {
                float swing = Mth.sin(ticks / Mth.PI) / (4.0F + ticks / 3.0F);
                if (bell.clickDirection == Direction.NORTH)
                {
                    xRot = -swing;
                }
                else if (bell.clickDirection == Direction.SOUTH)
                {
                    xRot = swing;
                }
                else if (bell.clickDirection == Direction.EAST)
                {
                    zRot = -swing;
                }
                else if (bell.clickDirection == Direction.WEST)
                {
                    zRot = swing;
                }
            }

            this.bellBody.xRot = xRot;
            this.bellBody.zRot = zRot;
            final Material mat = materials.computeIfAbsent(bellBlock.getTextureLocation(), res -> new Material(RenderHelpers.BLOCKS_ATLAS, res));
            final VertexConsumer buffer = mat.buffer(buffers, RenderType::entitySolid);
            this.bellBody.render(poseStack, buffer, packedLight, packedOverlay);
        }

    }
}
