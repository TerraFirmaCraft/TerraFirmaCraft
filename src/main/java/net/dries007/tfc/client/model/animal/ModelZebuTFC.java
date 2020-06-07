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
import net.dries007.tfc.objects.entity.animal.EntityZebuTFC;

/**
 * ModelZebuTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelZebuTFC extends ModelBase
{
        public ModelRenderer legLBack;
        public ModelRenderer bodyMain;
        public ModelRenderer udders;
        public ModelRenderer head;
        public ModelRenderer legRBack;
        public ModelRenderer legRFront;
        public ModelRenderer tailBody;
        public ModelRenderer legLFront;
        public ModelRenderer bodyBack;
        public ModelRenderer frontBody;
        public ModelRenderer hornMLeft1;
        public ModelRenderer hornMRight1;
        public ModelRenderer nose;
        public ModelRenderer hornFLeft;
        public ModelRenderer hornFRight;
        public ModelRenderer hornMLeft2;
        public ModelRenderer hornMRight2;
        public ModelRenderer hump;

        public ModelZebuTFC()
        {
            textureWidth = 64;
            textureHeight = 64;

            legRBack = new ModelRenderer(this, 0, 34);
            legRBack.setRotationPoint(-3.0F, 13.0F, 7.0F);
            legRBack.addBox(-2.0F, 0.0F, -2.0F, 4, 11, 4, 0.0F);
            hornFLeft = new ModelRenderer(this, 0, 0);
            hornFLeft.setRotationPoint(2.0F, -2.5F, -1.5F);
            hornFLeft.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
            setRotateAngle(hornFLeft, -0.4363323129985824F, 0.0F, 0.4363323129985824F);
            head = new ModelRenderer(this, 0, 6);
            head.setRotationPoint(0.0F, 5.0F, -8.0F);
            head.addBox(-3.0F, -3.0F, -5.0F, 6, 6, 5, 0.0F);
            hornMLeft1 = new ModelRenderer(this, 0, 18);
            hornMLeft1.mirror = true;
            hornMLeft1.setRotationPoint(5.0F, -3.4F, -2.0F);
            hornMLeft1.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
            setRotateAngle(hornMLeft1, 0.0F, 0.0F, 1.0471975511965976F);
            nose = new ModelRenderer(this, 4, 0);
            nose.setRotationPoint(0.0F, 2.0F, -5.0F);
            nose.addBox(-2.0F, -2.5F, -2.5F, 4, 3, 3, 0.0F);
            hornMRight2 = new ModelRenderer(this, 5, 18);
            hornMRight2.setRotationPoint(1.0F, 0.0F, -0.1F);
            hornMRight2.addBox(0.0F, -2.1F, -0.5F, 1, 3, 1, 0.0F);
            setRotateAngle(hornMRight2, 1.0908307824964558F, -1.3962634015954636F, 0.0F);
            hump = new ModelRenderer(this, 32, 1);
            hump.setRotationPoint(1.0F, 1.0F, 7.0F);
            hump.addBox(-5.0F, -2.0F, -1.5F, 8, 5, 2, 0.0F);
            hornMRight1 = new ModelRenderer(this, 0, 18);
            hornMRight1.setRotationPoint(-5.5F, -2.6F, -2.0F);
            hornMRight1.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
            setRotateAngle(hornMRight1, 0.0F, 0.0F, -1.0471975511965976F);
            legLBack = new ModelRenderer(this, 0, 34);
            legLBack.mirror = true;
            legLBack.setRotationPoint(3.0F, 13.0F, 7.0F);
            legLBack.addBox(-2.0F, 0.0F, -2.0F, 4, 11, 4, 0.0F);
            legRFront = new ModelRenderer(this, 0, 49);
            legRFront.setRotationPoint(-3.0F, 12.9F, -4.0F);
            legRFront.addBox(-2.0F, 0.0F, -2.0F, 4, 11, 4, 0.0F);
            hornMLeft2 = new ModelRenderer(this, 5, 18);
            hornMLeft2.mirror = true;
            hornMLeft2.setRotationPoint(0.0F, 0.0F, 0.9F);
            hornMLeft2.addBox(0.0F, -2.1F, -0.5F, 1, 3, 1, 0.0F);
            setRotateAngle(hornMLeft2, 1.0908307824964558F, 1.3962634015954636F, 0.0F);
            bodyMain = new ModelRenderer(this, 20, 43);
            bodyMain.setRotationPoint(0.0F, 8.5F, -2.5F);
            bodyMain.addBox(-6.0F, -5.0F, -4.0F, 12, 11, 10, 0.0F);
            frontBody = new ModelRenderer(this, 22, 26);
            frontBody.setRotationPoint(0.0F, 9.5F, -5.0F);
            frontBody.addBox(-4.0F, -3.0F, -6.0F, 8, 5, 12, 0.0F);
            setRotateAngle(frontBody, 1.5707963267948966F, 0.0F, 0.0F);
            udders = new ModelRenderer(this, 54, 7);
            udders.setRotationPoint(0.0F, 7.5F, 2.0F);
            udders.addBox(-2.0F, 0.0F, -8.0F, 4, 6, 1, 0.0F);
            setRotateAngle(udders, 1.5707963267948966F, 0.0F, 0.0F);
            bodyBack = new ModelRenderer(this, 24, 8);
            bodyBack.setRotationPoint(0.0F, 8.0F, 5.5F);
            bodyBack.addBox(-5.5F, -4.5F, -3.0F, 11, 11, 7, 0.0F);
            tailBody = new ModelRenderer(this, 24, 3);
            tailBody.setRotationPoint(0.0F, 4.5F, 9.0F);
            tailBody.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
            setRotateAngle(tailBody, 0.17453292519943295F, 0.0F, 0.0F);
            hornFRight = new ModelRenderer(this, 0, 0);
            hornFRight.setRotationPoint(-2.0F, -2.5F, -1.5F);
            hornFRight.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
            setRotateAngle(hornFRight, -0.4363323129985824F, 0.0F, -0.4363323129985824F);
            legLFront = new ModelRenderer(this, 0, 49);
            legLFront.mirror = true;
            legLFront.setRotationPoint(3.0F, 13.0F, -4.0F);
            legLFront.addBox(-2.0F, 0.0F, -2.0F, 4, 11, 4, 0.0F);

            head.addChild(hornFLeft);
            head.addChild(hornMLeft1);
            head.addChild(nose);
            hornMRight1.addChild(hornMRight2);
            frontBody.addChild(hump);
            head.addChild(hornMRight1);
            hornMLeft1.addChild(hornMLeft2);
            head.addChild(hornFRight);
        }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityZebuTFC zebu = ((EntityZebuTFC) entity);

        float percent = (float) zebu.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (zebu.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            udders.isHidden = true;
            hornFRight.isHidden = true;
            hornFLeft.isHidden = true;

        }
        else
        {
            hornMRight1.isHidden = true;
            hornMLeft1.isHidden = true;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head.render(par7);
        frontBody.render(par7);
        bodyMain.render(par7);
        bodyBack.render(par7);
        udders.render(par7);
        tailBody.render(par7);
        legRFront.render(par7);
        legLFront.render(par7);
        legRBack.render(par7);
        legLBack.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.legRFront.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legLFront.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legRBack.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legLBack.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        //horn1.rotateAngleX = 0F;
        //horn2.rotateAngleX = 0F;
        hornMRight1.isHidden = false;
        hornMLeft1.isHidden = false;
        hornFRight.isHidden = false;
        hornFLeft.isHidden = false;
        udders.isHidden = false;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}