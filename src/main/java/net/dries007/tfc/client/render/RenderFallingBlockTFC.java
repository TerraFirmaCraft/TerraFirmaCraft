/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;

@SideOnly(Side.CLIENT)
public class RenderFallingBlockTFC extends Render<EntityFallingBlockTFC>
{
    public RenderFallingBlockTFC(RenderManager manager)
    {
        super(manager);
        this.shadowSize = 0.5F;
    }

    @Override
    public void doRender(EntityFallingBlockTFC entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        IBlockState state = entity.getState();

        if (state == null) return;

        if (state.getRenderType() != EnumBlockRenderType.MODEL) return;

        World world = entity.world;
        if (state == world.getBlockState(new BlockPos(entity))) return;

        bindEntityTexture(entity);

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
        BlockPos posTop = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);
        GlStateManager.translate((float) (x - (double) posTop.getX() - 0.5D), (float) (y - (double) posTop.getY()), (float) (z - (double) posTop.getZ() - 0.5D));
        BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        brd.getBlockModelRenderer().renderModel(world, brd.getModelForState(state), state, posTop, bufferbuilder, false, MathHelper.getPositionRandom(entity.getOrigin()));
        tessellator.draw();

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityFallingBlockTFC entity)
    {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
