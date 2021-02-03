package net.dries007.tfc.client.render;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.dries007.tfc.common.tileentity.PotTileEntity;

@ParametersAreNonnullByDefault
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
        boolean useDefault = false;
        if (te.hasOutput() && te.getOutput().renderDefaultFluid())
        {
            useDefault = true;
        }
        else if (fluidStack.isEmpty())
        {
            return;
        }
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
        float height = 0.625F;
        
        builder.vertex(matrix4f, 0.3125F, height, 0.3125F).color(r, g, b, a).uv(sprite.getU(5), sprite.getV(5)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix4f, 0.3125F, height, 0.6875F).color(r, g, b, a).uv(sprite.getU(5), sprite.getV(11)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix4f, 0.6875F, height, 0.6875F).color(r, g, b, a).uv(sprite.getU(11), sprite.getV(11)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
        builder.vertex(matrix4f, 0.6875F, height, 0.3125F).color(r, g, b, a).uv(sprite.getU(11), sprite.getV(5)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
    }
}
