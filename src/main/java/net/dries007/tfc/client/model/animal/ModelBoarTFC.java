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
import net.dries007.tfc.objects.entity.animal.EntityBoarTFC;

/**
 * ModelBoarTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelBoarTFC extends ModelBase
{
    public ModelRenderer legLBack;
    public ModelRenderer tail;
    public ModelRenderer body;
    public ModelRenderer head;
    public ModelRenderer legRBack;
    public ModelRenderer legRFront;
    public ModelRenderer legLFront;
    public ModelRenderer bodyBHair;
    public ModelRenderer bodyLHair;
    public ModelRenderer bodyRHair;
    public ModelRenderer bodyFHair;
    public ModelRenderer nose;
    public ModelRenderer headLHair;
    public ModelRenderer headRHair;
    public ModelRenderer tuskL;
    public ModelRenderer tuskR;

    public ModelBoarTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        headRHair = new ModelRenderer(this, 0, -3);
        headRHair.setRotationPoint(-4.0F, 3.0F, -6.0F);
        headRHair.addBox(0.0F, 0.0F, 0.0F, 0, 1, 5, 0.0F);
        legRBack = new ModelRenderer(this, 0, 40);
        legRBack.setRotationPoint(-2.7F, 17.0F, 7.0F);
        legRBack.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        tuskR = new ModelRenderer(this, 43, 7);
        tuskR.setRotationPoint(-3.0F, -1.2F, -10.2F);
        tuskR.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotateAngle(tuskR, 0.2617993877991494F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 30, 21);
        head.setRotationPoint(0.0F, 11.0F, -6.0F);
        head.addBox(-4.0F, -4.0F, -8.0F, 8, 7, 7, 0.0F);
        legRFront = new ModelRenderer(this, 0, 52);
        legRFront.setRotationPoint(-2.7F, 17.0F, -5.0F);
        legRFront.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        bodyRHair = new ModelRenderer(this, 0, -14);
        bodyRHair.setRotationPoint(-5.0F, 8.0F, -8.0F);
        bodyRHair.addBox(0.0F, 0.0F, 0.0F, 0, 1, 18, 0.0F);
        setRotateAngle(bodyRHair, 1.5707963267948966F, 0.0F, 0.0F);
        bodyLHair = new ModelRenderer(this, 0, -14);
        bodyLHair.mirror = true;
        bodyLHair.setRotationPoint(5.0F, 8.0F, -8.0F);
        bodyLHair.addBox(0.0F, 0.0F, 0.0F, 0, 1, 18, 0.0F);
        setRotateAngle(bodyLHair, 1.5707963267948966F, 0.0F, 0.0F);
        bodyBHair = new ModelRenderer(this, 0, 0);
        bodyBHair.mirror = true;
        bodyBHair.setRotationPoint(-5.0F, 8.0F, -8.0F);
        bodyBHair.addBox(0.0F, 0.0F, 0.0F, 10, 0, 1, 0.0F);
        tail = new ModelRenderer(this, 26, 39);
        tail.setRotationPoint(0.0F, 10.0F, 9.4F);
        tail.addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        setRotateAngle(tail, 0.2617993877991494F, 0.0F, 0.0F);
        bodyFHair = new ModelRenderer(this, 0, 0);
        bodyFHair.setRotationPoint(-5.0F, -10.0F, -8.0F);
        bodyFHair.addBox(0.0F, 0.0F, 0.0F, 10, 0, 1, 0.0F);
        headLHair = new ModelRenderer(this, 0, -3);
        headLHair.mirror = true;
        headLHair.setRotationPoint(4.0F, 3.0F, -6.0F);
        headLHair.addBox(0.0F, 0.0F, 0.0F, 0, 1, 5, 0.0F);
        legLFront = new ModelRenderer(this, 0, 52);
        legLFront.mirror = true;
        legLFront.setRotationPoint(2.7F, 17.0F, -5.0F);
        legLFront.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        nose = new ModelRenderer(this, 36, 12);
        nose.setRotationPoint(0.0F, -0.5F, 0.0F);
        nose.addBox(-2.5F, -1.0F, -11.5F, 5, 4, 4, 0.0F);
        body = new ModelRenderer(this, 26, 36);
        body.setRotationPoint(0.0F, 11.0F, 2.0F);
        body.addBox(-5.0F, -10.0F, -7.0F, 10, 18, 9, 0.0F);
        setRotateAngle(body, 1.5707963267948966F, 0.0F, 0.0F);
        legLBack = new ModelRenderer(this, 0, 40);
        legLBack.mirror = true;
        legLBack.setRotationPoint(2.7F, 17.0F, 7.0F);
        legLBack.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        tuskL = new ModelRenderer(this, 43, 7);
        tuskL.mirror = true;
        tuskL.setRotationPoint(2.0F, -1.2F, -10.2F);
        tuskL.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotateAngle(tuskL, 0.2617993877991494F, 0.0F, 0.0F);

        head.addChild(headRHair);
        nose.addChild(tuskR);
        body.addChild(bodyRHair);
        body.addChild(bodyLHair);
        body.addChild(bodyBHair);
        body.addChild(bodyFHair);
        head.addChild(headLHair);
        head.addChild(nose);
        nose.addChild(tuskL);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        EntityBoarTFC hog = ((EntityBoarTFC) entity);

        float percent = (float) hog.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        if (hog.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            if (!hog.isChild())
            {
                tuskR.isHidden = false;
                tuskL.isHidden = false;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head.render(par7);
        body.render(par7);
        tail.render(par7);
        legRFront.render(par7);
        legLFront.render(par7);
        legRBack.render(par7);
        legRBack.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        tuskR.isHidden = true;
        tuskL.isHidden = true;
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.body.rotateAngleX = (float) Math.PI / 2F;
        this.legRFront.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legLFront.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legRBack.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legLBack.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}