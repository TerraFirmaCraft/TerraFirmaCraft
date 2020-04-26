/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.projectile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.projectile.EntityThrownJavelin;

@SideOnly(Side.CLIENT)
public class RenderThrownJavelin extends RenderThrownWeapon<EntityThrownJavelin>
{
    public RenderThrownJavelin(RenderManager renderManagerIn) { super(renderManagerIn); }

    @Override
    protected void doRenderTransformations(EntityThrownJavelin entity, float partialTicks)
    {
        GlStateManager.translate(0.0D, 0.40D, 0.0D);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 145.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.scale(2.0d, 2.0d, 1);
    }
}