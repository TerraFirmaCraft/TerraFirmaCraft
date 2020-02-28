/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelHorseTFC;

@SideOnly(Side.CLIENT)
public class RenderHorseTFC extends RenderHorse
{
    public RenderHorseTFC(RenderManager renderManager)
    {
        super(renderManager);
        this.mainModel = new ModelHorseTFC();
    }
}
