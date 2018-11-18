/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import net.dries007.tfc.objects.te.TEAnvilTFC;

public class TESRAnvil extends TileEntitySpecialRenderer<TEAnvilTFC>
{
    @Override
    public void render(TEAnvilTFC te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        // todo: render items on top of anvil (working item + hammer)
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
