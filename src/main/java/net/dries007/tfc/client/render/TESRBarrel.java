/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.client.FluidSpriteCache;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.te.TEBarrel;

public class TESRBarrel extends TileEntitySpecialRenderer<TEBarrel>
{
    @Override
    public void render(TEBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (!(state.getBlock() instanceof BlockBarrel) || state.getValue(BlockBarrel.SEALED))
        {
            return;
        }

        IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (fluidHandler == null || itemHandler == null)
        {
            return;
        }

        IFluidTankProperties properties = fluidHandler.getTankProperties()[0];
        FluidStack fluidStack = properties.getContents();
        ItemStack stack = itemHandler.getStackInSlot(TEBarrel.SLOT_ITEM);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5D, 0.15625D, 0.5D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate(90F, 1F, 0F, 0F);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        if (fluidStack != null)
        {
            Fluid fluid = fluidStack.getFluid();

            TextureAtlasSprite sprite = FluidSpriteCache.getStillSprite(fluid);

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            int color = fluid.getColor();

            float r = ((color >> 16) & 0xFF) / 255F;
            float g = ((color >> 8) & 0xFF) / 255F;
            float b = (color & 0xFF) / 255F;
            float a = ((color >> 24) & 0xFF) / 255F;

            GlStateManager.color(r, g, b, a);

            rendererDispatcher.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

            FluidStack content = properties.getContents();

            if (content == null)
            {
                return;
            }

            double height = 0.140625D + (0.75D - 0.015625D) * content.amount / properties.getCapacity();

            buffer.pos(0.1875D, height, 0.1875D).tex(sprite.getInterpolatedU(3), sprite.getInterpolatedV(3)).normal(0, 0, 1).endVertex();
            buffer.pos(0.1875D, height, 0.8125D).tex(sprite.getInterpolatedU(3), sprite.getInterpolatedV(13)).normal(0, 0, 1).endVertex();
            buffer.pos(0.8125D, height, 0.8125D).tex(sprite.getInterpolatedU(13), sprite.getInterpolatedV(13)).normal(0, 0, 1).endVertex();
            buffer.pos(0.8125D, height, 0.1875D).tex(sprite.getInterpolatedU(13), sprite.getInterpolatedV(3)).normal(0, 0, 1).endVertex();

            Tessellator.getInstance().draw();
        }

        GlStateManager.popMatrix();
    }
}
