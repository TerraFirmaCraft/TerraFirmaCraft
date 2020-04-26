/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.projectile;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.projectile.EntityThrownWeapon;

@SideOnly(Side.CLIENT)
public class RenderThrownWeapon<T extends EntityThrownWeapon> extends Render<T>
{
    private final RenderItem itemRenderer;

    public RenderThrownWeapon(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    public void doRender(@Nonnull T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();

        doRenderTransformations(entity, partialTicks);

        bindTexture(getEntityTexture(entity));

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(getTeamColor(entity));
        }

        ItemStack weapon = entity.getWeapon();
        if (!weapon.isEmpty())
        {
            this.itemRenderer.renderItem(weapon, ItemCameraTransforms.TransformType.GROUND);
        }

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull T entity) { return TextureMap.LOCATION_BLOCKS_TEXTURE; }

    protected void doRenderTransformations(T entity, float partialTicks)
    {
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 135.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.translate(-0.15D, -0.15D, 0.0D);
    }
}
