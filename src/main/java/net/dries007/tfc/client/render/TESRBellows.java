/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import net.dries007.tfc.objects.te.TEBellows;

import static net.dries007.tfc.Constants.META_TO_ANGLE;

public class TESRBellows extends TileEntitySpecialRenderer<TEBellows>
{

    @Override
    public void render(TEBellows te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        int dir = te.getBlockMetadata();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.03125D, z + 0.5D);
        GlStateManager.rotate(META_TO_ANGLE[dir], 0.0F, 1.0F, 0.0F);
        //TODO animation. Do not forget to remove "_inventory" inside the blockstate and uncomment the binding in clientregisterevent.
        GlStateManager.popMatrix();
    }
}
