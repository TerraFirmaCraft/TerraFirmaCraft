/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.ModelIngotPile;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.te.TEIngotPile;

import static net.dries007.tfc.Constants.MOD_ID;

public class TESRIngotPile extends TileEntitySpecialRenderer<TEIngotPile>
{

    private ModelIngotPile model = new ModelIngotPile();

    public TESRIngotPile()
    {
    }

    @Override
    public void render(TEIngotPile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        try
        {
            GlStateManager.color(1, 1, 1, 1);

            Metal metal = te.getMetal();
            int count = te.getCount();
            this.bindTexture(new ResourceLocation(MOD_ID, "textures/blocks/metal/" + metal.name() + ".png"));
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            // Render Ingot Pile here
            model.renderIngots(count);
        }
        finally
        {
            GlStateManager.popMatrix();
        }
    }
}
