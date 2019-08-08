/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelThatchBed extends ModelBase
{
    private ModelRenderer headPiece;
    private ModelRenderer footPiece;

    public ModelThatchBed()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.headPiece = new ModelRenderer(this, 0, 0);
        this.headPiece.addBox(0.0F, 0.0F, 0.0F, 16, 16, 9, 0.0F);
        this.footPiece = new ModelRenderer(this, 0, 25);
        this.footPiece.addBox(0.0F, 0.0F, 0.0F, 16, 16, 9, 0.0F);
    }

    public void render()
    {
        this.headPiece.render(0.0625F);
        this.footPiece.render(0.0625F);
    }

    public void preparePiece(boolean isHeadPiece)
    {
        this.headPiece.showModel = isHeadPiece;
        this.footPiece.showModel = !isHeadPiece;
    }
}
