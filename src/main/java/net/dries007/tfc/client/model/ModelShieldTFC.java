/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelShield;

public class ModelShieldTFC extends ModelShield
{
    public ModelRenderer plate;
    public ModelRenderer plate2;
    public ModelRenderer plate3;
    public ModelRenderer plate4;
    public ModelRenderer plate5;
    public ModelRenderer plate6;
    public ModelRenderer plate7;
    public ModelRenderer plate8;
    public ModelRenderer handle;

    public ModelShieldTFC()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.plate2 = new ModelRenderer(this, 0, 0);
        this.plate2.addBox(-6F, -11F, -2.0F, 12, 14, 1, 0.0F);
        this.plate3 = new ModelRenderer(this, 0, 20);
        this.plate3.addBox(-5F, 3F, -2.0F, 10, 1, 1, 0.0F);
        this.plate4 = new ModelRenderer(this, 0, 22);
        this.plate4.addBox(-4F, 4F, -2.0F, 8, 1, 1, 0.0F);
        this.plate5 = new ModelRenderer(this, 0, 24);
        this.plate5.addBox(-3F, 5F, -2.0F, 6, 1, 1, 0.0F);
        this.plate6 = new ModelRenderer(this, 0, 26);
        this.plate6.addBox(-2F, 6F, -2.0F, 4, 1, 1, 0.0F);
        this.plate7 = new ModelRenderer(this, 0, 28);
        this.plate7.addBox(-1F, 7F, -2.0F, 2, 1, 1, 0.0F);
        this.plate8 = new ModelRenderer(this, 0, 30);
        this.plate8.addBox(-0.5F, 8F, -2.0F, 1, 1, 1, 0.0F);
        this.handle = new ModelRenderer(this, 26, 0);
        this.handle.addBox(-1.0F, -4.0F, -1.0F, 2, 6, 6, 0.0F);
    }

    @Override
    public void render()
    {
        this.handle.render(0.0625F);
        this.plate2.render(0.0625F);
        this.plate3.render(0.0625F);
        this.plate4.render(0.0625F);
        this.plate5.render(0.0625F);
        this.plate6.render(0.0625F);
        this.plate7.render(0.0625F);
        this.plate8.render(0.0625F);
    }

}
