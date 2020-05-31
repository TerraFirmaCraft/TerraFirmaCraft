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
import net.dries007.tfc.objects.entity.animal.EntityDeerTFC;
import net.dries007.tfc.objects.entity.animal.EntityGazelleTFC;

/**
 * ModelGazelleTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelGazelleTFC extends ModelBase
{
    public ModelRenderer body;
    public ModelRenderer head;
    public ModelRenderer legRFront;
    public ModelRenderer rump;
    public ModelRenderer legLFront;
    public ModelRenderer collar;
    public ModelRenderer thighLBack;
    public ModelRenderer tail;
    public ModelRenderer neck;
    public ModelRenderer thighRBack;
    public ModelRenderer hornR1;
    public ModelRenderer earL;
    public ModelRenderer mouthTop;
    public ModelRenderer mouthBottom;
    public ModelRenderer earR;
    public ModelRenderer hornL1;
    public ModelRenderer hornR2;
    public ModelRenderer hornR3;
    public ModelRenderer hornL2;
    public ModelRenderer hornL3;
    public ModelRenderer legRFrontMiddle;
    public ModelRenderer legRFrontLower;
    public ModelRenderer legRFrontAnkle;
    public ModelRenderer legRFrontHoof;
    public ModelRenderer legLFrontMiddle;
    public ModelRenderer legLFrontLower;
    public ModelRenderer legLFrontAnkle;
    public ModelRenderer legLFrontHoof;
    public ModelRenderer thighLBackMiddle;
    public ModelRenderer thighLBackLower;
    public ModelRenderer thighLBackAnkle;
    public ModelRenderer thighLBackHoof;
    public ModelRenderer thighRBackMiddle;
    public ModelRenderer thighRBackLower;
    public ModelRenderer thighRBackAnkle;
    public ModelRenderer thighRBackHoof;
    private boolean running;

    public ModelGazelleTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        mouthBottom = new ModelRenderer(this, 2, 0);
        mouthBottom.setRotationPoint(0.0F, -6.8F, -6.3F);
        mouthBottom.addBox(-2.0F, -0.5F, -2.5F, 4, 1, 5, 0.0F);
        setRotateAngle(mouthBottom, 0.0F, 0.0F, 0.02722713633111154F);
        head = new ModelRenderer(this, 0, 12);
        head.setRotationPoint(0.0F, 2.5F, -7.5F);
        head.addBox(-2.5F, -11.0F, -5.5F, 5, 5, 6, 0.0F);
        setRotateAngle(head, 0.15707963267948966F, 0.0F, 0.0F);
        legRFrontAnkle = new ModelRenderer(this, 1, 34);
        legRFrontAnkle.mirror = true;
        legRFrontAnkle.setRotationPoint(0.0F, 5.4F, 0.8F);
        legRFrontAnkle.addBox(-0.5F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(legRFrontAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        hornR1 = new ModelRenderer(this, 55, 8);
        hornR1.setRotationPoint(-1.5F, -11.0F, -2.4F);
        hornR1.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornR1, -0.6108652381980153F, 0.0F, -0.2617993877991494F);
        collar = new ModelRenderer(this, 35, 11);
        collar.setRotationPoint(-2.5F, 2.5F, -7.8F);
        collar.addBox(0.0F, -2.0F, -4.0F, 5, 6, 7, 0.0F);
        setRotateAngle(collar, 1.1519173063162573F, 0.0F, 0.0F);
        mouthTop = new ModelRenderer(this, 3, 6);
        mouthTop.setRotationPoint(-0.5F, -0.6F, 0.9F);
        mouthTop.addBox(-1.5F, -9.3F, -9.0F, 4, 2, 4, 0.0F);
        setRotateAngle(mouthTop, 0.10471975511965977F, 0.0F, 0.0F);
        thighRBackMiddle = new ModelRenderer(this, 12, 42);
        thighRBackMiddle.setRotationPoint(-0.85F, 6.0F, 0.0F);
        thighRBackMiddle.addBox(0.0F, -1.0F, 0.0F, 2, 6, 3, 0.0F);
        setRotateAngle(thighRBackMiddle, 0.5585053606381855F, 0.0F, -0.05235987755982988F);
        earL = new ModelRenderer(this, 22, 22);
        earL.mirror = true;
        earL.setRotationPoint(0.0F, 0.0F, -2.0F);
        earL.addBox(4.0F, -10.0F, -2.0F, 5, 2, 1, 0.0F);
        setRotateAngle(earL, 0.0F, -0.5462880558742251F, -0.3490658503988659F);
        thighRBackHoof = new ModelRenderer(this, 13, 26);
        thighRBackHoof.setRotationPoint(0.0F, 5.0F, 0.4F);
        thighRBackHoof.addBox(-0.0F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(thighRBackHoof, -1.1344640137963142F, 0.0F, 0.0F);
        legLFrontLower = new ModelRenderer(this, 1, 37);
        legLFrontLower.mirror = true;
        legLFrontLower.setRotationPoint(0.0F, 7.0F, 0.0F);
        legLFrontLower.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        legLFrontMiddle = new ModelRenderer(this, 1, 46);
        legLFrontMiddle.mirror = true;
        legLFrontMiddle.setRotationPoint(0.0F, 3.0F, -0.6F);
        legLFrontMiddle.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        setRotateAngle(legLFrontMiddle, -0.3490658503988659F, 0.0F, -0.03490658503988659F);
        thighLBackAnkle = new ModelRenderer(this, 13, 29);
        thighLBackAnkle.mirror = true;
        thighLBackAnkle.setRotationPoint(0.0F, 5.5F, 0.5F);
        thighLBackAnkle.addBox(-1.0F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(thighLBackAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        hornL1 = new ModelRenderer(this, 55, 8);
        hornL1.mirror = true;
        hornL1.setRotationPoint(1.5F, -11.0F, -2.4F);
        hornL1.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornL1, -0.6108652381980153F, 0.0F, 0.2617993877991494F);
        hornR3 = new ModelRenderer(this, 55, 0);
        hornR3.setRotationPoint(0.15F, -2.7F, 0.15F);
        hornR3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornR3, -0.3490658503988659F, 0.0F, 0.2792526803190927F);
        hornL3 = new ModelRenderer(this, 55, 0);
        hornL3.mirror = true;
        hornL3.setRotationPoint(-0.15F, -2.7F, 0.15F);
        hornL3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornL3, -0.3490658503988659F, 0.0F, -0.2792526803190927F);
        body = new ModelRenderer(this, 26, 42);
        body.setRotationPoint(0.5F, 4.0F, -4.5F);
        body.addBox(-4.0F, -4.0F, -4.0F, 7, 10, 12, 0.0F);
        legRFrontLower = new ModelRenderer(this, 1, 37);
        legRFrontLower.setRotationPoint(0.0F, 7.0F, 0.0F);
        legRFrontLower.addBox(-0.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        thighLBackMiddle = new ModelRenderer(this, 12, 42);
        thighLBackMiddle.mirror = true;
        thighLBackMiddle.setRotationPoint(-0.15F, 6.0F, 0.0F);
        thighLBackMiddle.addBox(-1.0F, -1.0F, 0.0F, 2, 6, 3, 0.0F);
        setRotateAngle(thighLBackMiddle, 0.5585053606381855F, 0.0F, 0.05235987755982988F);
        thighLBackHoof = new ModelRenderer(this, 13, 26);
        thighLBackHoof.mirror = true;
        thighLBackHoof.setRotationPoint(0.0F, 5.0F, -0.4F);
        thighLBackHoof.addBox(-1.0F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(thighLBackHoof, -1.1344640137963142F, 0.0F, 0.0F);
        legLFront = new ModelRenderer(this, 0, 55);
        legLFront.mirror = true;
        legLFront.setRotationPoint(4.0F, 5.0F, -5.5F);
        legLFront.addBox(-1.25F, -1.0F, -1.5F, 2, 6, 3, 0.0F);
        setRotateAngle(legLFront, 0.3490658503988659F, 0.0F, 0.03490658503988659F);
        earR = new ModelRenderer(this, 22, 22);
        earR.mirror = true;
        earR.setRotationPoint(0.0F, 0.0F, -2.0F);
        earR.addBox(-9.0F, -10.0F, -2.0F, 5, 2, 1, 0.0F);
        setRotateAngle(earR, 0.0F, 0.3490658503988659F, 0.3490658503988659F);
        neck = new ModelRenderer(this, 16, 4);
        neck.setRotationPoint(0.0F, 2.5F, -7.8F);
        neck.addBox(-2.0F, -4.0F, -2.0F, 4, 5, 9, -0.2F);
        setRotateAngle(neck, 1.8151424220741026F, 0.0F, 0.0F);
        thighLBackLower = new ModelRenderer(this, 13, 32);
        thighLBackLower.mirror = true;
        thighLBackLower.setRotationPoint(-0.01F, 5.0F, 0.0F);
        thighLBackLower.addBox(-1.0F, -1.0F, 0.0F, 2, 8, 2, 0.0F);
        setRotateAngle(thighLBackLower, -0.3839724354387525F, 0.0F, 0.03490658503988659F);
        legRFront = new ModelRenderer(this, 0, 55);
        legRFront.setRotationPoint(-4.0F, 5.0F, -5.5F);
        legRFront.addBox(-0.7F, -1.0F, -1.5F, 2, 6, 3, 0.0F);
        setRotateAngle(legRFront, 0.3490658503988659F, 0.0F, -0.03490658503988659F);
        rump = new ModelRenderer(this, 32, 24);
        rump.setRotationPoint(0.0F, 4.5F, 0.0F);
        rump.addBox(-3.0F, -4.0F, 3.0F, 6, 9, 9, 0.0F);
        setRotateAngle(rump, -0.08726646259971647F, 0.0F, 0.0F);
        thighRBack = new ModelRenderer(this, 10, 51);
        thighRBack.setRotationPoint(-3.0F, 4.5F, 8.0F);
        thighRBack.addBox(-1.0F, -2.3F, -2.0F, 2, 8, 5, -0.0F);
        setRotateAngle(thighRBack, -0.17453292519943295F, 0.0F, 0.08726646259971647F);
        tail = new ModelRenderer(this, 24, 48);
        tail.setRotationPoint(0.0F, 2.0F, 11.5F);
        tail.addBox(-1.5F, -0.5F, 0.0F, 3, 2, 4, 0.0F);
        setRotateAngle(tail, -1.0471975511965976F, 0.0F, 0.0F);
        hornR2 = new ModelRenderer(this, 55, 4);
        hornR2.setRotationPoint(0.05F, -2.8F, 0.2F);
        hornR2.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornR2, -0.3490658503988659F, 0.0F, 0.1308996938995747F);
        legLFrontHoof = new ModelRenderer(this, 1, 31);
        legLFrontHoof.mirror = true;
        legLFrontHoof.setRotationPoint(0.0F, 5.0F, -0.5F);
        legLFrontHoof.addBox(-1.5F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(legLFrontHoof, -1.1344640137963142F, 0.0F, 0.0F);
        legRFrontHoof = new ModelRenderer(this, 1, 31);
        legRFrontHoof.setRotationPoint(0.0F, 5.0F, -0.5F);
        legRFrontHoof.addBox(-0.5F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(legRFrontHoof, -1.1344640137963142F, 0.0F, 0.0F);
        thighLBack = new ModelRenderer(this, 10, 51);
        thighLBack.mirror = true;
        thighLBack.setRotationPoint(3.0F, 4.5F, 8.0F);
        thighLBack.addBox(-1.0F, -2.3F, -2.0F, 2, 8, 5, -0.0F);
        setRotateAngle(thighLBack, -0.17453292519943295F, 0.0F, -0.08726646259971647F);
        legLFrontAnkle = new ModelRenderer(this, 1, 34);
        legLFrontAnkle.mirror = true;
        legLFrontAnkle.setRotationPoint(0.0F, 5.4F, 0.8F);
        legLFrontAnkle.addBox(-1.5F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(legLFrontAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        thighRBackLower = new ModelRenderer(this, 13, 32);
        thighRBackLower.setRotationPoint(0.01F, 5.0F, 0.0F);
        thighRBackLower.addBox(0.0F, -1.0F, 0.0F, 2, 8, 2, 0.0F);
        setRotateAngle(thighRBackLower, -0.3839724354387525F, 0.0F, -0.03490658503988659F);
        thighRBackAnkle = new ModelRenderer(this, 13, 29);
        thighRBackAnkle.mirror = true;
        thighRBackAnkle.setRotationPoint(0.0F, 6.3F, 0.2F);
        thighRBackAnkle.addBox(0.0F, 0.4F, -2.0F, 2, 1, 2, 0.0F);
        setRotateAngle(thighRBackAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        hornL2 = new ModelRenderer(this, 55, 4);
        hornL2.mirror = true;
        hornL2.setRotationPoint(-0.05F, -2.8F, 0.2F);
        hornL2.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornL2, -0.3490658503988659F, 0.0F, -0.1308996938995747F);
        legRFrontMiddle = new ModelRenderer(this, 1, 46);
        legRFrontMiddle.setRotationPoint(0.0F, 3.0F, -0.6F);
        legRFrontMiddle.addBox(-0.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        setRotateAngle(legRFrontMiddle, -0.3490658503988659F, 0.0F, 0.03490658503988659F);

        head.addChild(mouthBottom);
        legRFrontLower.addChild(legRFrontAnkle);
        head.addChild(hornR1);
        head.addChild(mouthTop);
        thighRBack.addChild(thighRBackMiddle);
        head.addChild(earL);
        thighRBackAnkle.addChild(thighRBackHoof);
        legLFrontMiddle.addChild(legLFrontLower);
        legLFront.addChild(legLFrontMiddle);
        thighLBackLower.addChild(thighLBackAnkle);
        head.addChild(hornL1);
        hornR2.addChild(hornR3);
        hornL2.addChild(hornL3);
        legRFrontMiddle.addChild(legRFrontLower);
        thighLBack.addChild(thighLBackMiddle);
        thighLBackAnkle.addChild(thighLBackHoof);
        head.addChild(earR);
        thighLBackMiddle.addChild(thighLBackLower);
        hornR1.addChild(hornR2);
        legLFrontAnkle.addChild(legLFrontHoof);
        legRFrontAnkle.addChild(legRFrontHoof);
        legLFrontLower.addChild(legLFrontAnkle);
        thighRBackMiddle.addChild(thighRBackLower);
        thighRBackLower.addChild(thighRBackAnkle);
        hornL1.addChild(hornL2);
        legRFront.addChild(legRFrontMiddle);
    }

    @Override
    public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        EntityGazelleTFC gazelle = ((EntityGazelleTFC) entity);

        running = false;
        float age = (float) (1f - gazelle.getPercentToAdulthood());

        float aa = 2F - (1.0F - age);
        GlStateManager.translate(0.0F, -6F * f5 * age / (float) Math.pow(aa, 0.4), 0);
        GlStateManager.pushMatrix();
        float ab = (float) Math.sqrt(1.0F / aa);
        GlStateManager.scale(ab, ab, ab);
        GlStateManager.translate(0.0F, 22F * f5 * age / (float) Math.pow(aa, 0.4), 2F * f5 * age / ab);

        head.render(f5);
        legRFront.render(f5);
        legLFront.render(f5);
        thighRBack.render(f5);
        thighLBack.render(f5);
        tail.render(f5);
        collar.render(f5);
        neck.render(f5);
        rump.render(f5);
        body.render(f5);
        rump.render(f5);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        f1 = Math.min(f1 * 7.5f, 0.75f);
        f *= 0.95f;

        setRotateAngle(head, f4 / (180F / (float) Math.PI) + 0.1570796F, f3 / (180F / (float) Math.PI), 0F);
        setRotateAngle(collar, f4 / (3 * (180F / (float) Math.PI)) + 1.151917F, f3 / (3 * (180F / (float) Math.PI)), 0F);
        setRotateAngle(neck, f4 / (1.5F * (180F / (float) Math.PI)) + 1.815142F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);
        //setRotateAngle(rump, -0.0872665F, 0F, 0F);
        //setRotateAngle(body, 1.43117F, 0F, 0F);

        setRotateAngle(legLFront, 0.3490659F, 0F, 0.0349066F);
        setRotateAngle(legRFront, 0.3490659F, 0F, -0.0349066F);
        setRotateAngle(thighRBack, -0.38397243F, 0.0F, -0.034906585F);
        setRotateAngle(thighLBack, -0.174532925F, 0.0F, -0.087266462F);

        if (!running)
        {
            setRotateAngle(legLFront, MathHelper.cos(f / 1.5F + 3F * (float) Math.PI / 2F) * 0.7F * f1 + 0.3490659F, 0F, 0.0349066F);
            setRotateAngle(legRFront, MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) * 0.7F * f1 + 0.3490659F, 0F, -0.0349066F);
            setRotateAngle(thighRBack, MathHelper.cos(f / 1.5F + (float) Math.PI * 7F / 4F) * 0.7F * f1 - 0.38397243F, 0.0F, -0.034906585F);
            setRotateAngle(thighLBack, MathHelper.cos(f / 1.5F + 3f * (float) Math.PI / 4F) * 0.7F * f1 - 0.174532925F, 0.0F, -0.087266462F);
            if (MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 0.7F * f1 > 0)
            {
                setRotateAngle(legRFrontLower, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 1.4F * f1, 0F, 0F);
                setRotateAngle(legRFrontMiddle, -MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 0.7F * f1 - 0.3490659F, 0F, 0.0349066F);
                setRotateAngle(legRFrontHoof, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F) * 2.1F * f1 + 1.134464F, 0, 0);
            }
            if (MathHelper.sin(f / 1.5F + 1F * (float) Math.PI / 2F) * 0.7F * f1 < 0)
            {
                setRotateAngle(legLFrontLower, MathHelper.sin(f / 1.5F + 3F * (float) Math.PI / 2F) * 1.4F * f1, 0F, 0F);
                setRotateAngle(legLFrontMiddle, -MathHelper.sin(f / 1.5F + 3F * (float) Math.PI / 2F) * 0.7F * f1 - 0.3490659F, 0F, -0.0349066F);
                setRotateAngle(legLFrontHoof, MathHelper.sin(f / 1.5F + 3F * (float) Math.PI / 2F) * 2.1F * f1 + 1.134464F, 0, 0);
            }
            if (MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 0.7F * f1 > 0)
            {
                setRotateAngle(thighRBackLower, MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 1.4F * f1 + 0.5585054F, 0F, -0.1745329F);
                setRotateAngle(thighRBackMiddle, -MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 1.4F * f1 - 22F / 180F * (float) Math.PI, 0F, 0F);
                setRotateAngle(thighRBackHoof, MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 2.1F * f1 + 1.134464F, 0F, 0F);
            }
            if (MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 0.7F * f1 > 0)
            {
                setRotateAngle(thighLBackLower, MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 1.4F * f1 + 0.5585054F, 0F, 0.1745329F);
                setRotateAngle(thighLBackMiddle, -MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 1.4F * f1 - 22F / 180F * (float) Math.PI, 0F, 0F);
                setRotateAngle(thighLBackHoof, MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 2.1F * f1 + 1.134464F, 0F, 0F);
            }

        }
        else
        {
            if (MathHelper.cos(f / 1.5F + 5 * (float) Math.PI / 4F) > -Math.sqrt(0.5) && MathHelper.cos(f / 1.5F + 5 * (float) Math.PI / 4F) < Math.sqrt(0.5))
            {
                setRotateAngle(legLFront, MathHelper.cos(f / 1.5F + 5F * (float) Math.PI / 4F) * 2.8F * f1 + 0.3490659F, 0F, 0.0349066F);
            }
            if (MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) > 0)
            {
                setRotateAngle(legRFrontLower, MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) * 3.5F * f1, 0F, 0F);
                setRotateAngle(legRFrontMiddle, -MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) * 3.5F * f1 - 0.3490659F, 0F, -0.0349066F);
                setRotateAngle(legRFrontHoof, MathHelper.sin(f / 1.5F + 5F * (float) Math.PI / 4F - 3F * (float) Math.PI / 8) * 2.1F * f1 + 1.134464F, 0, 0);
            }


            if (MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) > -Math.sqrt(0.5) && MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) < Math.sqrt(0.5))
            {
                setRotateAngle(legRFront, MathHelper.cos(f / 1.5F + (float) Math.PI / 2F) * 2.8F * f1 + 0.3490659F, 0F, -0.0349066F);
            }
            if (MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) > 0)
            {
                setRotateAngle(legLFrontLower, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) * 3.5F * f1, 0F, 0F);
                setRotateAngle(legLFrontMiddle, -MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) * 3.5F * f1 - 0.3490659F, 0F, 0.0349066F);
                setRotateAngle(legLFrontHoof, MathHelper.sin(f / 1.5F + (float) Math.PI / 2F - 3F * (float) Math.PI / 8) * 2.1F * f1 + 1.134464F, 0, 0);
            }

            setRotateAngle(thighRBack, MathHelper.cos(f / 1.5F + (float) Math.PI * 7F / 4F) * 2.8F * f1 - 0.38397243F, 0.0F, -0.034906585F);
            setRotateAngle(thighLBack, MathHelper.cos(f / 1.5F + 3f * (float) Math.PI / 4F) * 2.8F * f1 - 0.174532925F, 0.0F, -0.087266462F);
        }
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}