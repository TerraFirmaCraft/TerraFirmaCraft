/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityGoatTFC;

/**
 * ModelGoatTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelGoatTFC extends ModelBase
{
    private final ModelRenderer babyhorn1;
    private final ModelRenderer babyhorn2;
    private final ModelRenderer horn1f;
    private final ModelRenderer horn2f;
    private final ModelRenderer horn1ma;
    private final ModelRenderer horn1mb;
    private final ModelRenderer horn2ma;
    private final ModelRenderer horn2mb;
    private final ModelRenderer ear1;
    private final ModelRenderer ear2;
    private final ModelRenderer head1;
    private final ModelRenderer head2;
    private final ModelRenderer neck;
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer udders;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;


    public ModelGoatTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        babyhorn1 = new ModelRenderer(this, 44, 14);
        babyhorn1.addBox(-0.5F, 0F, -0.6F, 1, 2, 1);
        babyhorn1.setRotationPoint(-1.5F, -5F, 0.5F);
        babyhorn1.mirror = true;
        setRotation(babyhorn1, -0.593411F, 0F, 0F);

        babyhorn2 = new ModelRenderer(this, 44, 14);
        babyhorn2.addBox(-0.5F, 0F, -0.6F, 1, 2, 1);
        babyhorn2.setRotationPoint(1.5F, -5F, 0.5F);
        babyhorn2.mirror = true;
        setRotation(babyhorn2, -0.593411F, 0F, 0F);

        horn1f = new ModelRenderer(this, 50, 12);
        horn1f.addBox(-0.5F, -2.8F, -0.6F, 1, 4, 1);
        horn1f.setRotationPoint(-1.5F, -4F, 0.5F);
        horn1f.mirror = true;
        setRotation(horn1f, -0.593411F, 0F, 0F);


        horn2f = new ModelRenderer(this, 50, 12);
        horn2f.addBox(-0.5F, -2.8F, -0.6F, 1, 4, 1);
        horn2f.setRotationPoint(1.5F, -4F, 0.5F);
        horn2f.mirror = true;
        setRotation(horn2f, -0.593411F, 0F, 0F);

        horn1ma = new ModelRenderer(this, 56, 11);
        horn1ma.addBox(0F, 0F, 0F, 2, 4, 2, 0F);
        horn1ma.setRotationPoint(0F, -10F, 0.5F);
        horn1ma.setRotationPoint(-2.9F, -7F, 0F);
        setRotation(horn1ma, -0.546288F, -0.546288F, -0.0455532F);

        horn1mb = new ModelRenderer(this, 58, 7);
        horn1mb.addBox(0.5F, 1F, 0.5F, 1, 3, 1, 0.25F);
        horn1mb.setRotationPoint(0F, -2F, 4F);
        horn1mb.rotateAngleX = (float) -Math.PI / 3;

        horn2ma = new ModelRenderer(this, 56, 11);
        horn2ma.addBox(0F, 0F, 0F, 2, 4, 2, 0F);
        horn2ma.setRotationPoint(0F, -10F, 0.5F);
        horn2ma.setRotationPoint(1.2F, -7F, 0.75F);
        setRotation(horn2ma, -0.546288F, 0.546288F, 0.0455532F);

        horn2mb = new ModelRenderer(this, 58, 7);
        horn2mb.addBox(0.5F, 1F, 0.5F, 1, 3, 1, 0.25F);
        horn2mb.setRotationPoint(0F, -2F, 4F);
        horn2mb.rotateAngleX = (float) -Math.PI / 3;

        ear1 = new ModelRenderer(this, 28, 12);
        ear1.addBox(-1F, -0.5F, -1.5F, 1, 3, 3);
        ear1.setRotationPoint(-2.5F, 0F, 0F);
        setRotation(ear1, 0.273144F, -0.091106F, 0.591841F);

        ear2 = new ModelRenderer(this, 28, 12);
        ear2.addBox(0F, -0.5F, -1.5F, 1, 3, 3);
        ear2.setRotationPoint(2.5F, 0F, 0F);
        setRotation(ear2, 0.273144F, 0.091106F, -0.591841F);

        head1 = new ModelRenderer(this, 0, 6);
        head1.addBox(-3F, -2.0F, -6F, 6, 5, 8);
        head1.setRotationPoint(0F, 2.5F, -5.5F);
        setRotation(head1, 0.227590F, 0F, 0F);

        head2 = new ModelRenderer(this, 5, 0);
        head2.addBox(-2.5F, -1.0F, -3F, 5, 2, 4);
        head2.setRotationPoint(0F, -0.5F, -5F);
        setRotation(head2, 0.227590F, 0F, 0F);

        neck = new ModelRenderer(this, 1, 19);
        neck.addBox(-2F, -2F, -7F, 5, 4, 8);
        neck.setRotationPoint(-0.5F, 10F, -4F);
        setRotation(neck, -1.274788F, 0F, 0F);

        body = new ModelRenderer(this, 25, 40);
        body.addBox(-4F, -3F, -3F, 10, 16, 8);
        body.setRotationPoint(-1F, 11F, -3F);


        tail = new ModelRenderer(this, 13, 59);
        tail.addBox(-1.5F, 0F, 0F, 3, 3, 2);
        tail.setRotationPoint(0F, 9F, 8.5F);
        setRotation(tail, 0.819606F, 0F, 0F);

        udders = new ModelRenderer(this, 30, 31);
        udders.addBox(-3F, 0F, -2.5F, 6, 2, 6);
        udders.setRotationPoint(0F, 13.5F, 3.5F);
        setRotation(udders, 0F, 0F, 0F);

        leg1 = new ModelRenderer(this, 0, 40);
        leg1.addBox(-1F, -1F, -2F, 4, 12, 4);
        leg1.setRotationPoint(-3.6F, 13F, 7.2F);

        leg2 = new ModelRenderer(this, 0, 40);
        leg2.addBox(0F, -1F, -2F, 4, 12, 4);
        leg2.setRotationPoint(0.6F, 13F, 7.2F);

        leg3 = new ModelRenderer(this, 0, 40);
        leg3.addBox(-1F, -1F, -2F, 4, 12, 4);
        leg3.setRotationPoint(-3.6F, 13F, -2.5F);

        leg4 = new ModelRenderer(this, 0, 40);
        leg4.addBox(0F, -1F, -2F, 4, 12, 4);
        leg4.setRotationPoint(0.6F, 13F, -2.5F);

        this.head1.addChild(this.babyhorn1);
        this.head1.addChild(this.babyhorn2);
        this.horn1ma.addChild(this.horn1mb);
        this.horn2ma.addChild(this.horn2mb);
        this.head1.addChild(this.horn1ma);
        this.head1.addChild(this.horn2ma);
        this.head1.addChild(this.horn1f);
        this.head1.addChild(this.horn2f);
        this.head1.addChild(this.ear1);
        this.head1.addChild(this.ear2);

    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

        EntityGoatTFC goat = ((EntityGoatTFC) entity);

        float percent = (float) goat.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (goat.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            if (percent < 0.5)
            {
                babyhorn1.isHidden = false;
                babyhorn2.isHidden = false;
                horn1f.isHidden = true;
                horn2f.isHidden = true;
                horn1ma.isHidden = true;
                horn1mb.isHidden = true;
                horn2ma.isHidden = true;
                horn2mb.isHidden = true;
                udders.isHidden = true;
            }
            else if (percent < 0.75)

            {
                babyhorn1.isHidden = true;
                babyhorn2.isHidden = true;
                horn1f.isHidden = true;
                horn2f.isHidden = true;
                horn1ma.isHidden = false;
                horn1mb.isHidden = true;
                horn2ma.isHidden = false;
                horn2mb.isHidden = true;
                udders.isHidden = true;

            }
            else
            {
                babyhorn1.isHidden = true;
                babyhorn2.isHidden = true;
                udders.isHidden = true;
                horn1f.isHidden = true;
                horn2f.isHidden = true;
                horn1ma.isHidden = false;
                horn1mb.isHidden = false;
                horn2ma.isHidden = false;
                horn2mb.isHidden = false;
            }
        }

        else
        {
            if (percent < 0.5)
            {
                babyhorn1.isHidden = false;
                babyhorn2.isHidden = false;
                horn1f.isHidden = true;
                horn2f.isHidden = true;
                horn1ma.isHidden = true;
                horn1mb.isHidden = true;
                horn2ma.isHidden = true;
                horn2mb.isHidden = true;
                udders.isHidden = true;
            }
            else
            {
                babyhorn1.isHidden = true;
                babyhorn2.isHidden = true;
                horn1ma.isHidden = true;
                horn1mb.isHidden = true;
                horn2ma.isHidden = true;
                horn2mb.isHidden = true;
                horn1f.isHidden = false;
                horn2f.isHidden = false;
                udders.isHidden = false;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head1.render(par7);
        head2.render(par7);
        neck.render(par7);
        body.render(par7);
        udders.render(par7);
        leg1.render(par7);
        leg2.render(par7);
        leg3.render(par7);
        leg4.render(par7);
        tail.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head1.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head1.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.head2.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head2.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.body.rotateAngleX = (float) Math.PI / 2F;
        this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.2F * par2;
        this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.2F * par2;
        this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.2F * par2;
        this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.2F * par2;
        udders.isHidden = false;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}