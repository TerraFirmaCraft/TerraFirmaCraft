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
import net.dries007.tfc.objects.entity.animal.EntityYakTFC;

/**
 * ModelYakTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelYakTFC extends ModelBase
{
    public ModelRenderer bodyCenter;
    public ModelRenderer legBackLeft;
    public ModelRenderer legBackRight;
    public ModelRenderer legFrontRight;
    public ModelRenderer bodyRumpHair;
    public ModelRenderer bodyCenterHair;
    public ModelRenderer bodyRump;
    public ModelRenderer neck;
    public ModelRenderer head;
    public ModelRenderer legFrontLeft;
    public ModelRenderer tail;
    public ModelRenderer bodyShoulder;
    public ModelRenderer bodyCollar;
    public ModelRenderer udder;
    public ModelRenderer bodyShoulderHair;
    public ModelRenderer hornR1a;
    public ModelRenderer hornL1a;
    public ModelRenderer mouth;
    public ModelRenderer nose;
    public ModelRenderer headHair;
    public ModelRenderer hornR1b;
    public ModelRenderer hornR2;
    public ModelRenderer hornR1c;
    public ModelRenderer hornR1d;
    public ModelRenderer hornR3;
    public ModelRenderer hornL1b;
    public ModelRenderer hornL1c;
    public ModelRenderer hornL2;
    public ModelRenderer hornL1d;
    public ModelRenderer hornL3;
    public ModelRenderer teat1;
    public ModelRenderer teat2;
    public ModelRenderer teat3;
    public ModelRenderer teat4;
    public ModelRenderer teat5;
    public ModelRenderer teat6;
    public ModelRenderer hornRightF1A;
    public ModelRenderer hornRightF1B;
    public ModelRenderer hornRightF1C;
    public ModelRenderer hornRightF1D;
    public ModelRenderer hornRightF2;
    public ModelRenderer hornLeftF1A;
    public ModelRenderer hornLeftF1B;
    public ModelRenderer hornLeftF1C;
    public ModelRenderer hornLeftF1D;
    public ModelRenderer hornLeftF2;


    public ModelYakTFC()
    {
        textureWidth = 96;
        textureHeight = 96;

        neck = new ModelRenderer(this, 69, 7);
        neck.setRotationPoint(0.0F, 6.1F, -10.0F);
        neck.addBox(-2.5F, -2.0F, -3.0F, 5, 5, 3, 0.0F);
        setRotateAngle(neck, -0.17453292519943295F, 0.0F, 0.0F);
        udder = new ModelRenderer(this, 32, 24);
        udder.setRotationPoint(0.0F, 18.0F, 4.8F);
        udder.addBox(-2.5F, -0.5F, -3.5F, 5, 2, 8, 0.0F);
        bodyCollar = new ModelRenderer(this, 61, 15);
        bodyCollar.setRotationPoint(0.0F, 5.5F, -11.0F);
        bodyCollar.addBox(-3.5F, -2.5F, -2.5F, 7, 4, 9, -0.4F);
        setRotateAngle(bodyCollar, -1.3089969389957472F, 0.0F, 0.0F);
        hornL1c = new ModelRenderer(this, 41, 18);
        hornL1c.mirror = true;
        hornL1c.setRotationPoint(-1.5F, 0.3F, 0.3F);
        hornL1c.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornL1a = new ModelRenderer(this, 41, 18);
        hornL1a.mirror = true;
        hornL1a.setRotationPoint(3.5F, -2.2F, -2.8F);
        hornL1a.addBox(-1.5F, -0.8F, -0.8F, 3, 1, 1, 0.0F);
        setRotateAngle(hornL1a, 0.13962634015954636F, 0.0F, 0.0F);
        hornL1d = new ModelRenderer(this, 41, 18);
        hornL1d.mirror = true;
        hornL1d.setRotationPoint(-1.5F, 0.3F, -0.3F);
        hornL1d.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornL1b = new ModelRenderer(this, 41, 18);
        hornL1b.mirror = true;
        hornL1b.setRotationPoint(-1.5F, -0.3F, 0.3F);
        hornL1b.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        nose = new ModelRenderer(this, 3, 9);
        nose.setRotationPoint(0.0F, 2.0F, -3.0F);
        nose.addBox(-2.5F, -4.5F, -3.0F, 5, 3, 5, 0.0F);
        setRotateAngle(nose, 0.9599310885968813F, 0.0F, 0.0F);
        hornR1a = new ModelRenderer(this, 41, 18);
        hornR1a.setRotationPoint(-3.5F, -2.2F, -2.8F);
        hornR1a.addBox(-1.5F, -0.8F, -0.8F, 3, 1, 1, 0.0F);
        setRotateAngle(hornR1a, 0.13962634015954636F, 0.0F, 0.0F);
        bodyRumpHair = new ModelRenderer(this, 1, 50);
        bodyRumpHair.setRotationPoint(0.0F, 15.5F, 8.5F);
        bodyRumpHair.addBox(-6.0F, -3.0F, -3.5F, 12, 8, 7, 0.3F);
        head = new ModelRenderer(this, 2, 17);
        head.setRotationPoint(0.0F, 6.5F, -11.0F);
        head.addBox(-3.0F, -3.0F, -6.0F, 6, 5, 5, 0.1F);
        tail = new ModelRenderer(this, 39, 35);
        tail.setRotationPoint(0.0F, 10.2F, 11.7F);
        tail.addBox(-1.5F, -4.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotateAngle(tail, 0.17453292519943295F, 0.0F, 0.0F);
        bodyCenter = new ModelRenderer(this, 55, 49);
        bodyCenter.setRotationPoint(0.0F, 12.2F, 2.5F);
        bodyCenter.addBox(-5.5F, -8.4F, -8.0F, 11, 14, 11, 0.2F);
        setRotateAngle(bodyCenter, -0.13962634015954636F, 0.0F, 0.0F);
        hornR1d = new ModelRenderer(this, 41, 18);
        hornR1d.setRotationPoint(0.0F, -0.3F, 0.3F);
        hornR1d.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        legFrontRight = new ModelRenderer(this, 24, 0);
        legFrontRight.setRotationPoint(-4.0F, 15.5F, -6.0F);
        legFrontRight.addBox(-1.5F, -1.5F, -1.5F, 4, 10, 4, 0.0F);
        bodyShoulder = new ModelRenderer(this, 58, 74);
        bodyShoulder.setRotationPoint(0.0F, 10.5F, -5.5F);
        bodyShoulder.addBox(-6.0F, -8.0F, -5.0F, 12, 15, 7, 0.0F);
        setRotateAngle(bodyShoulder, 0.08726646259971647F, 0.0F, 0.0F);
        mouth = new ModelRenderer(this, 4, 0);
        mouth.setRotationPoint(0.0F, 2.7F, -2.8F);
        mouth.addBox(-3.0F, -2.5F, -3.0F, 6, 6, 3, 0.0F);
        setRotateAngle(mouth, -0.8726646259971648F, 0.0F, 0.0F);
        legBackRight = new ModelRenderer(this, 50, 0);
        legBackRight.setRotationPoint(-4.0F, 15.5F, 7.0F);
        legBackRight.addBox(-1.5F, -1.5F, -1.5F, 4, 10, 4, 0.0F);
        bodyShoulderHair = new ModelRenderer(this, 0, 80);
        bodyShoulderHair.setRotationPoint(0.0F, 15.5F, -6.2F);
        bodyShoulderHair.addBox(-6.0F, -3.0F, -4.1F, 12, 8, 8, 0.3F);
        hornR2 = new ModelRenderer(this, 42, 16);
        hornR2.setRotationPoint(-2.0F, -0.4F, 0.0F);
        hornR2.addBox(-1.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornR2, 0.2617993877991494F, 0.0F, 0.6108652381980153F);
        headHair = new ModelRenderer(this, 0, 27);
        headHair.setRotationPoint(0.0F, -1.0F, -1.5F);
        headHair.addBox(-3.5F, -2.5F, -5.0F, 7, 6, 6, 0.0F);
        hornR1b = new ModelRenderer(this, 41, 18);
        hornR1b.setRotationPoint(0.0F, 0.3F, -0.3F);
        hornR1b.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        legFrontLeft = new ModelRenderer(this, 24, 0);
        legFrontLeft.mirror = true;
        legFrontLeft.setRotationPoint(3.0F, 15.5F, -6.0F);
        legFrontLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 10, 4, 0.0F);
        hornR3 = new ModelRenderer(this, 41, 14);
        hornR3.setRotationPoint(-1.7F, -0.9F, 0.0F);
        hornR3.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornR3, 0.0F, 0.0F, 0.7853981633974483F);
        hornL2 = new ModelRenderer(this, 42, 16);
        hornL2.mirror = true;
        hornL2.setRotationPoint(2.0F, -0.4F, 0.0F);
        hornL2.addBox(-1.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(hornL2, 0.2617993877991494F, 0.0F, -0.6108652381980153F);
        legBackLeft = new ModelRenderer(this, 50, 0);
        legBackLeft.mirror = true;
        legBackLeft.setRotationPoint(3.0F, 15.5F, 7.0F);
        legBackLeft.addBox(-1.5F, -1.5F, -1.5F, 4, 10, 4, 0.0F);
        hornR1c = new ModelRenderer(this, 41, 18);
        hornR1c.setRotationPoint(0.0F, 0.3F, 0.3F);
        hornR1c.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        bodyCenterHair = new ModelRenderer(this, 1, 65);
        bodyCenterHair.setRotationPoint(0.0F, 15.5F, 2.2F);
        bodyCenterHair.addBox(-6.0F, -3.0F, -4.0F, 12, 8, 7, 0.2F);
        bodyRump = new ModelRenderer(this, 58, 28);
        bodyRump.setRotationPoint(0.0F, 6.3F, 8.0F);
        bodyRump.addBox(-6.0F, -2.5F, -2.5F, 12, 14, 7, 0.0F);
        setRotateAngle(bodyRump, -0.06981317007977318F, 0.0F, 0.0F);
        hornL3 = new ModelRenderer(this, 41, 14);
        hornL3.mirror = true;
        hornL3.setRotationPoint(1.7F, -0.9F, 0.0F);
        hornL3.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornL3, 0.0F, 0.0F, -0.7853981633974483F);
        teat1 = new ModelRenderer(this, 34, 30);
        teat1.setRotationPoint(1.0F, 1.4F, -2.5F);
        teat1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        teat2 = new ModelRenderer(this, 34, 30);
        teat2.setRotationPoint(1.0F, 1.4F, 0.0F);
        teat2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        teat3 = new ModelRenderer(this, 34, 30);
        teat3.setRotationPoint(1.0F, 1.4F, 2.5F);
        teat3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        teat4 = new ModelRenderer(this, 34, 30);
        teat4.setRotationPoint(-1.0F, 1.4F, -2.5F);
        teat4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        teat5 = new ModelRenderer(this, 34, 30);
        teat5.setRotationPoint(-1.0F, 1.4F, 0.0F);
        teat5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        teat6 = new ModelRenderer(this, 34, 30);
        teat6.setRotationPoint(-1.0F, 1.4F, 2.5F);
        teat6.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);

        hornRightF1A = new ModelRenderer(this, 41, 18);
        hornRightF1A.setRotationPoint(-3.5F, -2.2F, -2.8F);
        hornRightF1A.addBox(-1.5F, -0.8F, -0.8F, 3, 1, 1, 0.0F);
        setRotateAngle(hornRightF1A, -0.2617993877991494F, 0.2617993877991494F, 0.5235987755982988F);
        hornRightF1B = new ModelRenderer(this, 41, 18);
        hornRightF1B.setRotationPoint(0.0F, -0.3F, 0.3F);
        hornRightF1B.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornRightF1C = new ModelRenderer(this, 41, 18);
        hornRightF1C.setRotationPoint(0.0F, 0.3F, -0.3F);
        hornRightF1C.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornRightF1D = new ModelRenderer(this, 41, 18);
        hornRightF1D.setRotationPoint(0.0F, 0.3F, 0.3F);
        hornRightF1D.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornRightF2 = new ModelRenderer(this, 41, 21);
        hornRightF2.setRotationPoint(-2.0F, -0.4F, 0.0F);
        hornRightF2.addBox(-2.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornRightF2, 0.2617993877991494F, 0.0F, 0.6108652381980153F);


        hornLeftF1A = new ModelRenderer(this, 41, 18);
        hornLeftF1A.mirror = true;
        hornLeftF1A.setRotationPoint(3.5F, -2.2F, -2.8F);
        hornLeftF1A.addBox(-1.5F, -0.8F, -0.8F, 3, 1, 1, 0.0F);
        setRotateAngle(hornLeftF1A, -0.2617993877991494F, -0.2617993877991494F, -0.5235987755982988F);
        hornLeftF1B = new ModelRenderer(this, 41, 18);
        hornLeftF1B.mirror = true;
        hornLeftF1B.setRotationPoint(-1.5F, -0.3F, 0.3F);
        hornLeftF1B.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornLeftF1C = new ModelRenderer(this, 41, 18);
        hornLeftF1C.mirror = true;
        hornLeftF1C.setRotationPoint(-1.5F, 0.3F, -0.3F);
        hornLeftF1C.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornLeftF1D = new ModelRenderer(this, 41, 18);
        hornLeftF1D.mirror = true;
        hornLeftF1D.setRotationPoint(-1.5F, 0.3F, 0.3F);
        hornLeftF1D.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        hornLeftF2 = new ModelRenderer(this, 41, 21);
        hornLeftF2.mirror = true;
        hornLeftF2.setRotationPoint(2.0F, -0.4F, 0.0F);
        hornLeftF2.addBox(-1.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(hornLeftF2, 0.2617993877991494F, 0.0F, -0.6108652381980153F);

        hornL1a.addChild(hornL1c);
        head.addChild(hornL1a);
        hornL1a.addChild(hornL1d);
        hornL1a.addChild(hornL1b);
        head.addChild(nose);
        head.addChild(hornR1a);
        hornR1a.addChild(hornR1d);
        head.addChild(mouth);
        hornR1a.addChild(hornR2);
        head.addChild(headHair);
        hornR1a.addChild(hornR1b);
        hornR2.addChild(hornR3);
        hornL1a.addChild(hornL2);
        hornR1a.addChild(hornR1c);
        hornL2.addChild(hornL3);
        udder.addChild(teat1);
        udder.addChild(teat2);
        udder.addChild(teat3);
        udder.addChild(teat4);
        udder.addChild(teat5);
        udder.addChild(teat6);

        head.addChild(hornRightF1A);
        hornRightF1A.addChild(hornRightF1B);
        hornRightF1A.addChild(hornRightF1C);
        hornRightF1A.addChild(hornRightF1D);
        hornRightF1A.addChild(hornRightF2);
        head.addChild(hornLeftF1A);
        hornLeftF1A.addChild(hornLeftF1B);
        hornLeftF1A.addChild(hornLeftF1C);
        hornLeftF1A.addChild(hornLeftF1D);
        hornLeftF1A.addChild(hornLeftF2);
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
            if (percent < 0.5)
            {
                udder.isHidden = true;
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
                hornR1a.isHidden = true;
                hornR1b.isHidden = true;
                hornR1c.isHidden = true;
                hornR1d.isHidden = true;
                hornR2.isHidden = true;
                hornR3.isHidden = true;
                hornL1a.isHidden = true;
                hornL1b.isHidden = true;
                hornL1c.isHidden = true;
                hornL1d.isHidden = true;
                hornL2.isHidden = true;
                hornL3.isHidden = true;
            }
            if (percent < 0.75)
            {
                udder.isHidden = true;
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
                hornR2.isHidden = true;
                hornR3.isHidden = true;
                hornL2.isHidden = true;
                hornL3.isHidden = true;
            }
            else
            {
                udder.isHidden = true;
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
            }
        }

        else
        {
            if (percent < 0.5)
            {
                udder.isHidden = true;
                hornRightF1A.isHidden = true;
                hornRightF1B.isHidden = true;
                hornRightF1C.isHidden = true;
                hornRightF1D.isHidden = true;
                hornRightF2.isHidden = true;
                hornLeftF1A.isHidden = true;
                hornLeftF1B.isHidden = true;
                hornLeftF1C.isHidden = true;
                hornLeftF1D.isHidden = true;
                hornLeftF2.isHidden = true;
                hornR1a.isHidden = true;
                hornR1b.isHidden = true;
                hornR1c.isHidden = true;
                hornR1d.isHidden = true;
                hornR2.isHidden = true;
                hornR3.isHidden = true;
                hornL1a.isHidden = true;
                hornL1b.isHidden = true;
                hornL1c.isHidden = true;
                hornL1d.isHidden = true;
                hornL2.isHidden = true;
                hornL3.isHidden = true;
            }
            if (percent < 0.75)
            {
                hornRightF2.isHidden = true;
                hornLeftF2.isHidden = true;
                hornR1a.isHidden = true;
                hornR1b.isHidden = true;
                hornR1c.isHidden = true;
                hornR1d.isHidden = true;
                hornR2.isHidden = true;
                hornR3.isHidden = true;
                hornL1a.isHidden = true;
                hornL1b.isHidden = true;
                hornL1c.isHidden = true;
                hornL1d.isHidden = true;
                hornL2.isHidden = true;
                hornL3.isHidden = true;
            }
            else
            {
                hornR1a.isHidden = true;
                hornR1b.isHidden = true;
                hornR1c.isHidden = true;
                hornR1d.isHidden = true;
                hornR2.isHidden = true;
                hornR3.isHidden = true;
                hornL1a.isHidden = true;
                hornL1b.isHidden = true;
                hornL1c.isHidden = true;
                hornL1d.isHidden = true;
                hornL2.isHidden = true;
                hornL3.isHidden = true;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / ageScale, 1 / ageScale, 1 / ageScale);
        GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);

        head.render(par7);
        neck.render(par7);
        udder.render(par7);
        bodyCollar.render(par7);
        bodyRumpHair.render(par7);
        tail.render(par7);
        bodyCenter.render(par7);
        legFrontRight.render(par7);
        bodyShoulder.render(par7);
        legBackRight.render(par7);
        bodyShoulderHair.render(par7);
        legFrontLeft.render(par7);
        legBackLeft.render(par7);
        bodyCenterHair.render(par7);
        bodyRump.render(par7);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity ent)
    {
        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);

        this.legFrontRight.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.legFrontLeft.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legBackRight.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.legBackLeft.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;

        udder.isHidden = false;
        hornR1a.isHidden = false;
        hornR1b.isHidden = false;
        hornR1c.isHidden = false;
        hornR1d.isHidden = false;
        hornR2.isHidden = false;
        hornR3.isHidden = false;
        hornL1a.isHidden = false;
        hornL1b.isHidden = false;
        hornL1c.isHidden = false;
        hornL1d.isHidden = false;
        hornL2.isHidden = false;
        hornL3.isHidden = false;
        hornRightF1A.isHidden = false;
        hornRightF1B.isHidden = false;
        hornRightF1C.isHidden = false;
        hornRightF1D.isHidden = false;
        hornRightF2.isHidden = false;
        hornLeftF1A.isHidden = false;
        hornLeftF1B.isHidden = false;
        hornLeftF1C.isHidden = false;
        hornLeftF1D.isHidden = false;
        hornLeftF2.isHidden = false;
    }

    private void setRotateAngle(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}