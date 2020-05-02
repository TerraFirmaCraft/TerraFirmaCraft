/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntitySheepTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelSheepBodyTFC extends ModelQuadruped
{
    private final ModelRenderer horn1;
    private final ModelRenderer horn2;
    private final ModelRenderer horn1b;
    private final ModelRenderer horn2b;

    public ModelSheepBodyTFC()
    {
        super(12, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
        this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
        this.body = new ModelRenderer(this, 28, 8);
        this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
        this.body.setRotationPoint(0.0F, 5.0F, 2.0F);

        horn1 = new ModelRenderer(this, 28, 2);
        horn1.addBox(0F, 0F, 0F, 2, 4, 2, 0F);
        horn1.setRotationPoint(0F, -10F, 0F);
        horn1.rotateAngleZ = (float) -Math.PI / 6;
        horn1.rotateAngleX = (float) -Math.PI / 6;
        horn1.rotateAngleY = (float) -Math.PI / 3;
        horn1.setRotationPoint(-5F, -6F, -1F);

        horn1b = new ModelRenderer(this, 38, 4);
        horn1b.addBox(0.5F, 1F, 0.5F, 1, 3, 1, 0.25F);
        horn1b.setRotationPoint(0F, -2F, 4F);
        horn1b.rotateAngleX = (float) -Math.PI / 3;

        horn2 = new ModelRenderer(this, 28, 2);
        horn2.addBox(0F, 0F, 0F, 2, 4, 2, 0F);
        horn2.setRotationPoint(0F, -10F, 0F);
        horn2.rotateAngleZ = (float) Math.PI / 6;
        horn2.rotateAngleX = (float) -Math.PI / 6;
        horn2.rotateAngleY = (float) Math.PI / 3;
        horn2.setRotationPoint(4F, -6.5F, 0.75F);

        horn2b = new ModelRenderer(this, 38, 4);
        horn2b.addBox(0.5F, 1F, 0.5F, 1, 3, 1, 0.25F);
        horn2b.setRotationPoint(0F, -2F, 4F);
        horn2b.rotateAngleX = (float) -Math.PI / 3;

        horn1.addChild(horn1b);
        horn2.addChild(horn2b);

        head.addChild(horn1);
        head.addChild(horn2);
    }

    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntitySheepTFC sheep = ((EntitySheepTFC) entity);

        float percent = (float) sheep.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (percent < 0.5)
        {
            horn1.isHidden = true;
            horn2.isHidden = true;
            if (percent < 0.75)
            {
                horn1b.isHidden = true;
                horn2b.isHidden = true;
            }
        }

        if (sheep.getGender() == EntityAnimalTFC.Gender.FEMALE)
        {
            horn1.isHidden = true;
            horn2.isHidden = true;
        }


        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        this.head.render(par7);
        this.body.render(par7);
        this.leg1.render(par7);
        this.leg2.render(par7);
        this.leg3.render(par7);
        this.leg4.render(par7);
        horn1.isHidden = false;
        horn1b.isHidden = false;
        horn2.isHidden = false;
        horn2b.isHidden = false;
        GlStateManager.popMatrix();
    }
}