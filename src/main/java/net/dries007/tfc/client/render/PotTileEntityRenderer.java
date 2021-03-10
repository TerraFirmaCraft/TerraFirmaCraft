package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.dries007.tfc.common.tileentity.PotTileEntity;

import static net.dries007.tfc.common.tileentity.PotTileEntity.SLOT_EXTRA_INPUT_END;
import static net.dries007.tfc.common.tileentity.PotTileEntity.SLOT_EXTRA_INPUT_START;

public class PotTileEntityRenderer extends TileEntityRenderer<PotTileEntity>
{

    public PotTileEntityRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(PotTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (te.getLevel() == null) return;
        FluidStack fluidStack = te.getFluidContained();
        boolean useDefault = te.hasOutput() && te.getOutput().renderDefaultFluid();
        if (!fluidStack.isEmpty() || useDefault)
        {
            Fluid fluid = useDefault ? Fluids.WATER : fluidStack.getFluid();

            FluidAttributes attributes = fluid.getAttributes();
            ResourceLocation texture = attributes.getStillTexture(fluidStack);
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(texture);
            int color = attributes.getColor();

            float r = ((color >> 16) & 0xFF) / 255F;
            float g = ((color >> 8) & 0xFF) / 255F;
            float b = (color & 0xFF) / 255F;
            float a = ((color >> 24) & 0xFF) / 255F;

            if (useDefault)
            {
                b = 0;
                g /= 4;
                r *= 3;
            }

            IVertexBuilder builder = buffer.getBuffer(RenderType.entityTranslucentCull(AtlasTexture.LOCATION_BLOCKS));
            Matrix4f matrix4f = matrixStack.last().pose();

            builder.vertex(matrix4f, 0.3125F, 0.625F, 0.3125F).color(r, g, b, a).uv(sprite.getU(5), sprite.getV(5)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
            builder.vertex(matrix4f, 0.3125F, 0.625F, 0.6875F).color(r, g, b, a).uv(sprite.getU(5), sprite.getV(11)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
            builder.vertex(matrix4f, 0.6875F, 0.625F, 0.6875F).color(r, g, b, a).uv(sprite.getU(11), sprite.getV(11)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
            builder.vertex(matrix4f, 0.6875F, 0.625F, 0.3125F).color(r, g, b, a).uv(sprite.getU(11), sprite.getV(5)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        }

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
            {
                ItemStack item = cap.getStackInSlot(i);
                if (!item.isEmpty())
                {
                    float yOffset = 0.46f;
                    matrixStack.pushPose();
                    matrixStack.translate(0.5, 0.003125D + yOffset, 0.5);
                    matrixStack.scale(0.3f, 0.3f, 0.3f);
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees(90F));
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

                    int ordinal = i - SLOT_EXTRA_INPUT_START;
                    matrixStack.translate(0, 0, -0.12F * ordinal);

                    Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
                    matrixStack.popPose();
                }
            }
        });
    }
}
