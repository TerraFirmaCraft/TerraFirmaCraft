/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.model.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;

/**
 * ModelAlpacaBodyTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelAlpacaBodyTFC extends ModelBase
{
    private final ModelRenderer ear1f;
    private final ModelRenderer ear2f;
    private final ModelRenderer ear1m;
    private final ModelRenderer ear2m;
    private final ModelRenderer nose;
    private final ModelRenderer head;
    private final ModelRenderer neck1;
    private final ModelRenderer neck2;
    private final ModelRenderer body;
    private final ModelRenderer tailf;
    private final ModelRenderer tailm;
    private final ModelRenderer udders;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;

    public ModelAlpacaBodyTFC()
    {
        textureWidth = 128;
        textureHeight = 64;

        ear1f = new ModelRenderer(this, 0, 9);
        ear1f.addBox(-1F, -1.5F, -1F, 2, 2, 2);
        ear1f.setRotationPoint(-2.1F, -2.8F, 1.5F);
        setRotation(ear1f, 0.136659F, -0.273144F, -0.273144F);

        ear2f = new ModelRenderer(this, 0, 9);
        ear2f.addBox(-1F, -1.5F, -1F, 2, 2, 2);
        ear2f.setRotationPoint(2.1F, -2.8F, 1.5F);
        setRotation(ear2f, 0.136659F, 0.273144F, 0.273144F);
        ear2f.mirror = true;

        ear1m = new ModelRenderer(this, 28, 8);
        ear1m.addBox(-1F, -2F, -1F, 2, 3, 2);
        ear1m.setRotationPoint(-2.1F, -3.3F, 1.5F);
        setRotation(ear1m, 0.136659F, -0.273144F, -0.273144F);

        ear2m = new ModelRenderer(this, 28, 8);
        ear2m.addBox(-1F, -2F, -1F, 2, 3, 2);
        ear2m.setRotationPoint(2.1F, -3.3F, 1.5F);
        setRotation(ear2m, 0.136659F, 0.273144F, 0.273144F);
        ear2m.mirror = true;

        nose = new ModelRenderer(this, 10, 0);
        nose.addBox(-2F, -1.5F, -1.5F, 4, 3, 3);
        nose.setRotationPoint(0F, 1.4F, -4.5F);

        head = new ModelRenderer(this, 4, 6);
        head.addBox(-3.0F, -2.5F, -3.51F, 6, 6, 7);
        head.setRotationPoint(0.0F, -4F, -9.4F);

        neck1 = new ModelRenderer(this, 50, 13);
        neck1.addBox(-2F, -3.5F, -2.5F, 4, 7, 5);
        neck1.setRotationPoint(0.001F, 8.5F, -6.9F);
        setRotation(neck1, 0.591841F, 0F, 0F);

        neck2 = new ModelRenderer(this, 51, 0);
        neck2.addBox(-1.5F, -4F, -2.5F, 4, 9, 4);
        neck2.setRotationPoint(-0.5F, 2F, -8.43F);

        body = new ModelRenderer(this, 98, 0);
        body.addBox(-4.0F, -10.0F, -7.0F, 9, 17, 6, 0.0F);
        body.setRotationPoint(-0.5F, 6.0F, 2.0F);

        tailf = new ModelRenderer(this, 99, 23);
        tailf.addBox(-1F, -1F, -1.5F, 2, 2, 3);
        tailf.setRotationPoint(0F, 9.5F, 9.6F);

        tailm = new ModelRenderer(this, 113, 23);
        tailm.addBox(-1.5F, -1.5F, -1.5F, 3, 4, 3);
        tailm.setRotationPoint(0F, 9.5F, 9.6F);

        udders = new ModelRenderer(this, 0, 28);
        udders.addBox(-2F, 0F, -3F, 5, 2, 6);
        udders.setRotationPoint(0F, 13F, 3F);

        leg1 = new ModelRenderer(this, 82, 7);
        leg1.addBox(-1F, -1F, -2F, 4, 12, 4);
        leg1.setRotationPoint(-4F, 13F, 6.99F);

        leg2 = new ModelRenderer(this, 82, 7);
        leg2.addBox(0F, -1F, -2F, 4, 12, 4);
        leg2.setRotationPoint(1F, 13F, 6.99F);

        leg3 = new ModelRenderer(this, 82, 7);
        leg3.addBox(-1F, -1F, -2F, 4, 12, 4);
        leg3.setRotationPoint(-4F, 13F, -4F);

        leg4 = new ModelRenderer(this, 82, 7);
        leg4.addBox(0F, -1F, -2F, 4, 12, 4);
        leg4.setRotationPoint(1F, 13F, -4F);


        head.addChild(ear1f);
        head.addChild(ear2f);
        head.addChild(ear1m);
        head.addChild(ear2m);
        head.addChild(nose);
    }

    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityAlpacaTFC alpaca = ((EntityAlpacaTFC) entity);

        float percent = (float) alpaca.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (alpaca.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            udders.isHidden = true;
            ear1f.isHidden = true;
            ear2f.isHidden = true;
            ear1m.isHidden = false;
            ear2m.isHidden = false;
            tailf.isHidden = true;
            tailm.isHidden = false;
        }

        else
        {
            udders.isHidden = false;
            ear1f.isHidden = false;
            ear2f.isHidden = false;
            ear1m.isHidden = true;
            ear2m.isHidden = true;
            tailf.isHidden = false;
            tailm.isHidden = true;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head.render(par7);
        neck2.render(par7);
        neck1.render(par7);
        body.render(par7);
        tailf.render(par7);
        tailm.render(par7);
        udders.render(par7);
        leg1.render(par7);
        leg2.render(par7);
        leg3.render(par7);
        leg4.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
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