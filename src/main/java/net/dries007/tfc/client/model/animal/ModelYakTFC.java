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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityMuskOxTFC;
import net.dries007.tfc.objects.entity.animal.EntityYakTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelYakTFC extends ModelBase
{
    public ModelRenderer bodyMain;
    public ModelRenderer legBackLeft;
    public ModelRenderer legBackRight;
    public ModelRenderer legFrontLeft;
    public ModelRenderer tail;
    public ModelRenderer legFrontRight;
    public ModelRenderer bodyShoulder;
    public ModelRenderer neck;
    public ModelRenderer head;
    public ModelRenderer bodyLowerHair;
    public ModelRenderer udder;
    public ModelRenderer bodyLower;
    public ModelRenderer hornL1;
    public ModelRenderer snout;
    public ModelRenderer headHair;
    public ModelRenderer hornR1;
    public ModelRenderer hornL2;
    public ModelRenderer hornL3;
    public ModelRenderer hornR2;
    public ModelRenderer hornR3;

    public ModelYakTFC()
    {
        textureWidth = 128;
        textureHeight = 128;

        headHair = new ModelRenderer(this, 0, 24);
        headHair.setRotationPoint(0.0F, -2.5F, -1.0F);
        headHair.addBox(-3.0F, -2.5F, -5.0F, 6, 6, 7, 0.0F);
        legBackRight = new ModelRenderer(this, 44, 113);
        legBackRight.setRotationPoint(-4.5F, 14.5F, 7.0F);
        legBackRight.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        tail = new ModelRenderer(this, 37, 100);
        tail.setRotationPoint(0.0F, 10.2F, 11.6F);
        tail.addBox(-1.5F, -4.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotateAngle(tail, 0.2617993877991494F, 0.0F, 0.0F);
        udder = new ModelRenderer(this, 22, 3);
        udder.setRotationPoint(0.0F, 18.5F, 5.5F);
        udder.addBox(-2.5F, -0.5F, -3.5F, 5, 1, 8, 0.0F);
        hornR3 = new ModelRenderer(this, 41, 26);
        hornR3.setRotationPoint(-1.7F, -0.9F, 0.0F);
        hornR3.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornR3, 0.0F, 0.0F, 0.7853981633974483F);
        head = new ModelRenderer(this, 0, 37);
        head.setRotationPoint(0.0F, 6.5F, -11.3F);
        head.addBox(-3.0F, -4.5F, -6.0F, 6, 5, 7, -0.3F);
        snout = new ModelRenderer(this, 3, 14);
        snout.setRotationPoint(0.0F, 1.7F, -2.5F);
        snout.addBox(-2.5F, -4.5F, -3.0F, 5, 5, 5, 0.0F);
        setRotateAngle(snout, 1.0850711959648747F, 0.0F, 0.0F);
        hornL3 = new ModelRenderer(this, 41, 26);
        hornL3.mirror = true;
        hornL3.setRotationPoint(1.7F, -0.9F, 0.0F);
        hornL3.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornL3, 0.0F, 0.0F, -0.7853981633974483F);
        legBackLeft = new ModelRenderer(this, 27, 113);
        legBackLeft.mirror = true;
        legBackLeft.setRotationPoint(3.5F, 14.5F, 7.0F);
        legBackLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        neck = new ModelRenderer(this, 3, 49);
        neck.setRotationPoint(0.0F, 6.5F, -11.0F);
        neck.addBox(-2.5F, -2.5F, -2.5F, 5, 7, 5, -0.4F);
        setRotateAngle(neck, 0.8726646259971648F, 0.0F, 0.0F);
        bodyLowerHair = new ModelRenderer(this, 62, 61);
        bodyLowerHair.setRotationPoint(0.0F, 13.0F, -1.0F);
        bodyLowerHair.addBox(-6.5F, -8.0F, -8.0F, 13, 14, 20, 0.2F);
        legFrontRight = new ModelRenderer(this, 44, 113);
        legFrontRight.setRotationPoint(-4.5F, 14.5F, -6.0F);
        legFrontRight.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        hornR2 = new ModelRenderer(this, 42, 28);
        hornR2.setRotationPoint(-2.0F, -0.4F, 0.0F);
        hornR2.addBox(-1.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornR2, 0.0F, 0.0F, 0.6108652381980153F);
        hornR1 = new ModelRenderer(this, 40, 30);
        hornR1.setRotationPoint(-2.9F, -3.0F, -2.0F);
        hornR1.addBox(-1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
        legFrontLeft = new ModelRenderer(this, 27, 113);
        legFrontLeft.mirror = true;
        legFrontLeft.setRotationPoint(3.5F, 14.5F, -6.0F);
        legFrontLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 11, 4, 0.0F);
        bodyLower = new ModelRenderer(this, 62, 95);
        bodyLower.setRotationPoint(0.0F, 13.0F, -1.0F);
        bodyLower.addBox(-6.5F, -8.0F, -8.0F, 13, 13, 20, 0.0F);
        hornL1 = new ModelRenderer(this, 40, 30);
        hornL1.mirror = true;
        hornL1.setRotationPoint(2.9F, -3.0F, -2.0F);
        hornL1.addBox(-1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
        bodyShoulder = new ModelRenderer(this, 75, 2);
        bodyShoulder.setRotationPoint(0.0F, 10.0F, -6.0F);
        bodyShoulder.addBox(-5.0F, -8.0F, -5.0F, 10, 14, 10, 0.0F);
        bodyMain = new ModelRenderer(this, 67, 26);
        bodyMain.setRotationPoint(0.0F, 11.0F, 0.0F);
        bodyMain.addBox(-4.0F, -8.0F, -8.0F, 8, 15, 20, 0.0F);
        hornL2 = new ModelRenderer(this, 42, 28);
        hornL2.mirror = true;
        hornL2.setRotationPoint(2.0F, -0.4F, 0.0F);
        hornL2.addBox(-1.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornL2, 0.0F, 0.0F, -0.6108652381980153F);

        head.addChild(headHair);
        hornR2.addChild(hornR3);
        head.addChild(snout);
        hornL2.addChild(hornL3);
        hornR1.addChild(hornR2);
        head.addChild(hornR1);
        head.addChild(hornL1);
        hornL1.addChild(hornL2);
    }

    @Override
    public void render(@Nonnull Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        EntityYakTFC yak = ((EntityYakTFC) entity);

        float percent = (float) yak.getPercentToAdulthood();
        float ageScale = 2.0F - percent;

        if (yak.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            //udders.isHidden = true;
        }
        else
        {
            //horn.isHidden = true;
            //horn2b.isHidden = true;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        legBackRight.render(par7);
        tail.render(par7);
        udder.render(par7);
        head.render(par7);
        legBackLeft.render(par7);
        neck.render(par7);
        bodyLowerHair.render(par7);
        legFrontRight.render(par7);
        legFrontLeft.render(par7);
        bodyLower.render(par7);
        bodyShoulder.render(par7);
        bodyMain.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        /*this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.body.rotateAngleX = (float) Math.PI / 2F;
        this.udders.rotateAngleX = (float) Math.PI / 2F;
        this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        horn1.rotateAngleX = 0F;
        horn2.rotateAngleX = 0F;
        horn1.isHidden = false;
        horn1b.isHidden = false;
        horn2.isHidden = false;
        horn2b.isHidden = false;
        udders.isHidden = false;

         */
    }

    private void setRotateAngle(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}