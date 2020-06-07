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

import net.dries007.tfc.objects.entity.animal.EntityWildebeestTFC;

/**
 * ModelWildebeestTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelWildebeestTFC extends ModelBase
{
    public ModelRenderer body;
    public ModelRenderer legRFront;
    public ModelRenderer rump;
    public ModelRenderer legLFront;
    public ModelRenderer collar;
    public ModelRenderer thighLBack;
    public ModelRenderer tail;
    public ModelRenderer headBase;
    public ModelRenderer neck;
    public ModelRenderer thighRBack;
    public ModelRenderer bodyMid;
    public ModelRenderer legRFrontMiddle;
    public ModelRenderer legRFrontLower;
    public ModelRenderer legRFrontAnkle;
    public ModelRenderer legRFrontHoof;
    public ModelRenderer legLFrontMiddle;
    public ModelRenderer legLFrontLower;
    public ModelRenderer legLFrontAnkle;
    public ModelRenderer legLFrontHoof;
    public ModelRenderer thighLMiddle;
    public ModelRenderer thighLLower;
    public ModelRenderer thighLAnkle;
    public ModelRenderer thighLHoof;
    public ModelRenderer head;
    public ModelRenderer hornR1;
    public ModelRenderer hornL1;
    public ModelRenderer hornR2;
    public ModelRenderer hornR3;
    public ModelRenderer hornL2;
    public ModelRenderer hornL3;
    public ModelRenderer thighRMiddle;
    public ModelRenderer thighRLower;
    public ModelRenderer thighRAnkle;
    public ModelRenderer thighRHoof;
    private boolean running;

    public ModelWildebeestTFC()
    {
        textureWidth = 64;
        textureHeight = 64;

        tail = new ModelRenderer(this, 21, 44);
        tail.setRotationPoint(0.0F, 1.5F, 11.5F);
        tail.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 8, 0.0F);
        setRotateAngle(tail, -1.3962634015954636F, 0.0F, 0.0F);
        thighLHoof = new ModelRenderer(this, 13, 26);
        thighLHoof.mirror = true;
        thighLHoof.setRotationPoint(0.0F, 5.0F, -0.4F);
        thighLHoof.addBox(-1.0F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(thighLHoof, -1.1344640137963142F, 0.0F, 0.0F);
        thighRAnkle = new ModelRenderer(this, 13, 29);
        thighRAnkle.mirror = true;
        thighRAnkle.setRotationPoint(0.0F, 6.3F, 0.2F);
        thighRAnkle.addBox(0.0F, 0.4F, -2.0F, 2, 1, 2, 0.0F);
        setRotateAngle(thighRAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        thighRMiddle = new ModelRenderer(this, 12, 42);
        thighRMiddle.setRotationPoint(-0.85F, 6.0F, 0.0F);
        thighRMiddle.addBox(0.0F, -1.0F, 0.0F, 2, 6, 3, 0.0F);
        setRotateAngle(thighRMiddle, 0.5585053606381855F, 0.0F, -0.05235987755982988F);
        rump = new ModelRenderer(this, 33, 13);
        rump.setRotationPoint(0.0F, 4.5F, 0.8F);
        rump.addBox(-3.0F, -4.0F, 3.0F, 7, 9, 8, 0.0F);
        setRotateAngle(rump, 0.03490658503988659F, 0.0F, 0.0F);
        legRFrontAnkle = new ModelRenderer(this, 1, 34);
        legRFrontAnkle.mirror = true;
        legRFrontAnkle.setRotationPoint(0.0F, 5.4F, 0.8F);
        legRFrontAnkle.addBox(-1.0F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(legRFrontAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        thighRBack = new ModelRenderer(this, 10, 51);
        thighRBack.setRotationPoint(-3.0F, 4.5F, 8.0F);
        thighRBack.addBox(-1.0F, -2.299999952316284F, -2.0F, 2, 8, 5, -1.1920928955078125E-7F);
        setRotateAngle(thighRBack, -0.3839724361896515F, 0.0F, -0.03490658476948738F);
        thighRLower = new ModelRenderer(this, 13, 32);
        thighRLower.setRotationPoint(0.01F, 5.0F, 0.0F);
        thighRLower.addBox(0.0F, -1.0F, 0.0F, 2, 8, 2, 0.0F);
        setRotateAngle(thighRLower, -0.3839724354387525F, 0.0F, -0.03490658503988659F);
        thighRHoof = new ModelRenderer(this, 13, 26);
        thighRHoof.setRotationPoint(0.0F, 5.0F, 0.4F);
        thighRHoof.addBox(-0.0F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(thighRHoof, -1.1344640137963142F, 0.0F, 0.0F);
        thighLAnkle = new ModelRenderer(this, 13, 29);
        thighLAnkle.mirror = true;
        thighLAnkle.setRotationPoint(0.0F, 5.5F, 0.5F);
        thighLAnkle.addBox(-1.0F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(thighLAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        hornR1 = new ModelRenderer(this, 19, 6);
        hornR1.setRotationPoint(-0.5F, -2.5F, -0.5F);
        hornR1.addBox(-0.5F, -3.5F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(hornR1, 0.0F, 0.0F, -1.3962634015954636F);
        hornL3 = new ModelRenderer(this, 19, 0);
        hornL3.mirror = true;
        hornL3.setRotationPoint(-0.85F, -2.3F, 0.0F);
        hornL3.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornL3, 0.0F, 0.0F, -0.6981317007977318F);
        legLFrontLower = new ModelRenderer(this, 1, 37);
        legLFrontLower.mirror = true;
        legLFrontLower.setRotationPoint(0.0F, 7.0F, 0.0F);
        legLFrontLower.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        bodyMid = new ModelRenderer(this, 35, 30);
        bodyMid.setRotationPoint(0.5F, 5.0F, 2.2F);
        bodyMid.addBox(-4.0F, -5.0F, -3.0F, 8, 10, 5, -0.1F);
        headBase = new ModelRenderer(this, 0, 21);
        headBase.setRotationPoint(-0.5F, -0.6F, -11.5F);
        headBase.addBox(-2.0F, -3.0F, -1.8F, 5, 1, 3, 0.0F);
        neck = new ModelRenderer(this, 20, 8);
        neck.setRotationPoint(0.0F, 0.4F, -8.7F);
        neck.addBox(-2.0F, -4.0F, -2.0F, 4, 6, 6, -0.2F);
        setRotateAngle(neck, 2.443460952792061F, 0.0F, 0.0F);
        legLFrontMiddle = new ModelRenderer(this, 1, 46);
        legLFrontMiddle.mirror = true;
        legLFrontMiddle.setRotationPoint(0.0F, 3.0F, -0.6F);
        legLFrontMiddle.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        setRotateAngle(legLFrontMiddle, -0.3490658503988659F, 0.0F, -0.03490658503988659F);
        thighLMiddle = new ModelRenderer(this, 12, 42);
        thighLMiddle.mirror = true;
        thighLMiddle.setRotationPoint(-0.15F, 6.0F, 0.0F);
        thighLMiddle.addBox(-1.0F, -1.0F, 0.0F, 2, 6, 3, 0.0F);
        setRotateAngle(thighLMiddle, 0.5585053606381855F, 0.0F, 0.05235987755982988F);
        legRFrontMiddle = new ModelRenderer(this, 1, 46);
        legRFrontMiddle.setRotationPoint(0.0F, 3.0F, -0.6F);
        legRFrontMiddle.addBox(-1.0F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        setRotateAngle(legRFrontMiddle, -0.3490658503988659F, 0.0F, 0.03490658503988659F);
        legRFrontLower = new ModelRenderer(this, 1, 37);
        legRFrontLower.setRotationPoint(0.0F, 7.0F, 0.0F);
        legRFrontLower.addBox(-1.0F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        body = new ModelRenderer(this, 32, 45);
        body.setRotationPoint(0.5F, 3.0F, -4.5F);
        body.addBox(-4.0F, -4.0F, -4.0F, 8, 11, 8, 0.0F);
        setRotateAngle(body, -0.03490658503988659F, 0.0F, 0.0F);
        thighLBack = new ModelRenderer(this, 10, 51);
        thighLBack.mirror = true;
        thighLBack.setRotationPoint(3.0F, 4.5F, 8.0F);
        thighLBack.addBox(-1.0F, -2.299999952316284F, -2.0F, 2, 8, 5, -1.1920928955078125E-7F);
        setRotateAngle(thighLBack, -0.17453292012214658F, 0.0F, -0.08726646006107329F);
        hornL1 = new ModelRenderer(this, 19, 6);
        hornL1.mirror = true;
        hornL1.setRotationPoint(1.5F, -2.5F, -0.5F);
        hornL1.addBox(-0.5F, -3.5F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(hornL1, 0.0F, 0.0F, 1.3962634015954636F);
        legLFrontAnkle = new ModelRenderer(this, 1, 34);
        legLFrontAnkle.mirror = true;
        legLFrontAnkle.setRotationPoint(0.0F, 5.4F, 0.8F);
        legLFrontAnkle.addBox(-1.5F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(legLFrontAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        legRFrontHoof = new ModelRenderer(this, 1, 31);
        legRFrontHoof.setRotationPoint(0.0F, 5.0F, -0.5F);
        legRFrontHoof.addBox(-1.0F, 0.0F, -6.300000190734863F, 2, 1, 2, 0.0F);
        setRotateAngle(legRFrontHoof, -1.1344640254974365F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 0, 9);
        head.setRotationPoint(0.0F, -0.8F, -1.4F);
        head.addBox(-2.0F, -2.0F, -2.0F, 5, 8, 4, 0.0F);
        setRotateAngle(head, -0.3490658503988659F, 0.0F, 0.0F);
        legRFront = new ModelRenderer(this, 0, 55);
        legRFront.setRotationPoint(-3.5F, 5.0F, -5.5F);
        legRFront.addBox(-1.0F, -1.0F, -1.5F, 2, 6, 3, 0.0F);
        setRotateAngle(legRFront, 0.349065899848938F, 0.0F, -0.03490659967064857F);
        hornL2 = new ModelRenderer(this, 19, 3);
        hornL2.mirror = true;
        hornL2.setRotationPoint(-0.2F, -3.4F, 0.0F);
        hornL2.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornL2, 0.0F, 0.0F, -0.9075712110370513F);
        thighLLower = new ModelRenderer(this, 13, 32);
        thighLLower.mirror = true;
        thighLLower.setRotationPoint(-0.01F, 5.0F, 0.0F);
        thighLLower.addBox(-1.0F, -1.0F, 0.0F, 2, 8, 2, 0.0F);
        setRotateAngle(thighLLower, -0.3839724354387525F, 0.0F, 0.03490658503988659F);
        hornR2 = new ModelRenderer(this, 19, 3);
        hornR2.setRotationPoint(0.2F, -3.4F, 0.0F);
        hornR2.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornR2, 0.0F, 0.0F, 0.9075712110370513F);
        legLFrontHoof = new ModelRenderer(this, 1, 31);
        legLFrontHoof.mirror = true;
        legLFrontHoof.setRotationPoint(0.0F, 5.0F, -0.5F);
        legLFrontHoof.addBox(-1.0F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(legLFrontHoof, -1.1344640137963142F, 0.0F, 0.0F);
        legLFront = new ModelRenderer(this, 0, 55);
        legLFront.mirror = true;
        legLFront.setRotationPoint(4.5F, 5.0F, -5.5F);
        legLFront.addBox(-1.0F, -1.0F, -1.5F, 2, 6, 3, 0.0F);
        setRotateAngle(legLFront, 0.349065899848938F, 0.0F, 0.03490659967064857F);
        collar = new ModelRenderer(this, 36, 0);
        collar.setRotationPoint(0.0F, 1.5F, -8.0F);
        collar.addBox(-2.5F, -2.0F, -4.0F, 5, 6, 7, 0.0F);
        setRotateAngle(collar, 1.1519173063162573F, 0.0F, 0.0F);
        hornR3 = new ModelRenderer(this, 19, 0);
        hornR3.setRotationPoint(0.85F, -2.3F, 0.0F);
        hornR3.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornR3, 0.0F, 0.0F, 0.6981317007977318F);

        thighLAnkle.addChild(thighLHoof);
        thighRLower.addChild(thighRAnkle);
        thighRBack.addChild(thighRMiddle);
        legRFrontLower.addChild(legRFrontAnkle);
        thighRMiddle.addChild(thighRLower);
        thighRAnkle.addChild(thighRHoof);
        thighLLower.addChild(thighLAnkle);
        headBase.addChild(hornR1);
        hornL2.addChild(hornL3);
        legLFrontMiddle.addChild(legLFrontLower);
        legLFront.addChild(legLFrontMiddle);
        thighLBack.addChild(thighLMiddle);
        legRFront.addChild(legRFrontMiddle);
        legRFrontMiddle.addChild(legRFrontLower);
        headBase.addChild(hornL1);
        legLFrontLower.addChild(legLFrontAnkle);
        legRFrontAnkle.addChild(legRFrontHoof);
        headBase.addChild(head);
        hornL1.addChild(hornL2);
        thighLMiddle.addChild(thighLLower);
        hornR1.addChild(hornR2);
        legLFrontAnkle.addChild(legLFrontHoof);
        hornR2.addChild(hornR3);
    }

    @Override
    public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        EntityWildebeestTFC wildebeest = ((EntityWildebeestTFC) entity);

        running = false;
        float age = (float) (1f - wildebeest.getPercentToAdulthood());

        float aa = 2F - (1.0F - age);
        GlStateManager.translate(0.0F, -6F * f5 * age / (float) Math.pow(aa, 0.4), 0);
        GlStateManager.pushMatrix();
        float ab = (float) Math.sqrt(1.0F / aa);
        GlStateManager.scale(ab, ab, ab);
        GlStateManager.translate(0.0F, 22F * f5 * age / (float) Math.pow(aa, 0.4), 2F * f5 * age / ab);

        headBase.render(f5);
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

        setRotateAngle(headBase, f4 / (180F / (float) Math.PI) + 0.0F, f3 / (180F / (float) Math.PI), 0F);
        setRotateAngle(collar, f4 / (3 * (180F / (float) Math.PI)) + 1.151917F, f3 / (3 * (180F / (float) Math.PI)), 0F);
        setRotateAngle(neck, f4 / (1.5F * (180F / (float) Math.PI)) + 1.815142F, f3 / (1.5F * (180F / (float) Math.PI)), 0F);

        setRotateAngle(neck, 2.443460952792061F, 0.0F, 0.0F);
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
                setRotateAngle(thighRLower, MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 1.4F * f1 + 0.5585054F, 0F, -0.1745329F);
                setRotateAngle(thighRMiddle, -MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 1.4F * f1 - 22F / 180F * (float) Math.PI, 0F, 0F);
                setRotateAngle(thighRHoof, MathHelper.sin(f / 1.5F + (float) Math.PI * 7F / 4F) * 2.1F * f1 + 1.134464F, 0F, 0F);
            }
            if (MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 0.7F * f1 > 0)
            {
                setRotateAngle(thighLLower, MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 1.4F * f1 + 0.5585054F, 0F, 0.1745329F);
                setRotateAngle(thighLMiddle, -MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 1.4F * f1 - 22F / 180F * (float) Math.PI, 0F, 0F);
                setRotateAngle(thighLHoof, MathHelper.sin(f / 1.5F + 3f * (float) Math.PI / 4F) * 2.1F * f1 + 1.134464F, 0F, 0F);
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