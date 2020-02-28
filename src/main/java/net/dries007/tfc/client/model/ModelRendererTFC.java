/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRendererTFC extends ModelRenderer
{
    public ModelBase modelBase;
    public int textureOffsetX;
    public int textureOffsetY;

    public ModelRendererTFC(ModelBase par1)
    {
        super(par1);
        modelBase = par1;
    }

    public ModelRendererTFC(ModelBase par1ModelBase, int par2, int par3)
    {
        this(par1ModelBase);
        this.setTextureOffset(par2, par3);
    }
}
