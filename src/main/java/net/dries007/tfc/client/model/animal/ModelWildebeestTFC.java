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
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.IAnimalTFC;

/**
 * ModelWildebeestTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelWildebeestTFC extends ModelBase
{
    public ModelRenderer headBase;
    public ModelRenderer earL;
    public ModelRenderer earR;
    public ModelRenderer headFaceA;
    public ModelRenderer headFaceB;
    public ModelRenderer noseA;
    public ModelRenderer noseB;
    public ModelRenderer headBottom;
    public ModelRenderer bodyMid;
    public ModelRenderer body;
    public ModelRenderer rump;
    public ModelRenderer collar;
    public ModelRenderer neck;
    public ModelRenderer hornRBase;
    public ModelRenderer hornLBase;
    public ModelRenderer hornR2A;
    public ModelRenderer hornR2B;
    public ModelRenderer hornR3;
    public ModelRenderer hornR4;
    public ModelRenderer hornL2A;
    public ModelRenderer hornL2B;
    public ModelRenderer hornL3;
    public ModelRenderer hornL4;
    public ModelRenderer tailBase;
    public ModelRenderer tailTip;
    public ModelRenderer tailHairA;
    public ModelRenderer tailHairB;
    public ModelRenderer tailHairC;
    public ModelRenderer tailHairD;
    public ModelRenderer tailHairE;
    public ModelRenderer legLFront;
    public ModelRenderer thighLBack;
    public ModelRenderer legLFrontMid;
    public ModelRenderer legLFrontBottom;
    public ModelRenderer legLFrontAnkle;
    public ModelRenderer legLFrontHoof;
    public ModelRenderer thighLBackMid;
    public ModelRenderer thighLBackBottom;
    public ModelRenderer thighLBackAnkle;
    public ModelRenderer thighLBackHoof;
    public ModelRenderer legRFront;
    public ModelRenderer thighRBack;
    public ModelRenderer legRFrontMid;
    public ModelRenderer legRFrontBottom;
    public ModelRenderer legRFrontAnkle;
    public ModelRenderer legRFrontHoof;
    public ModelRenderer thighRBackMid;
    public ModelRenderer thighRBackBottom;
    public ModelRenderer thighRBackAnkle;
    public ModelRenderer thighRBackHoof;

    public ModelWildebeestTFC()
    {
        textureWidth = 80;
        textureHeight = 80;

        tailHairA = new ModelRenderer(this, 30, 43);
        tailHairA.setRotationPoint(-0.7F, -0.3F, 1.2F);
        tailHairA.addBox(0.0F, -1.0F, -0.5F, 0, 2, 4, 0.0F);
        setRotateAngle(tailHairA, 0.0F, 0.0F, -0.6981317007977318F);
        noseA = new ModelRenderer(this, 5, 0);
        noseA.setRotationPoint(0.2F, 7.6F, -0.5F);
        noseA.addBox(-2.5F, -2.0F, -2.0F, 3, 2, 2, 0.0F);
        setRotateAngle(noseA, -0.3490658503988659F, 0.0F, 0.0F);
        hornL3 = new ModelRenderer(this, 30, 3);
        hornL3.mirror = true;
        hornL3.setRotationPoint(-0.2F, -3.6F, 0.25F);
        hornL3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornL3, 0.08726646259971647F, -0.08726646259971647F, -0.6981317007977318F);
        neck = new ModelRenderer(this, 23, 22);
        neck.setRotationPoint(0.0F, 0.8F, -10.0F);
        neck.addBox(-2.0F, -4.0F, -2.0F, 4, 7, 5, -0.2F);
        setRotateAngle(neck, 2.530727415391778F, 0.0F, 0.0F);
        headFaceB = new ModelRenderer(this, 4, 15);
        headFaceB.mirror = true;
        headFaceB.setRotationPoint(1.3F, -0.6F, -2.4F);
        headFaceB.addBox(-2.0F, -2.0F, -2.0F, 3, 8, 3, 0.0F);
        setRotateAngle(headFaceB, -0.13962634015954636F, 0.0F, 0.0F);
        hornR2B = new ModelRenderer(this, 30, 6);
        hornR2B.setRotationPoint(-0.26F, 0.55F, 0.37F);
        hornR2B.addBox(-0.5F, -3.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornR2B, -0.5235987755982988F, -1.3962634015954636F, -0.6981317007977318F);
        tailTip = new ModelRenderer(this, 29, 53);
        tailTip.setRotationPoint(0.0F, -0.15F, 8.4F);
        tailTip.addBox(-0.5F, -0.5F, -1.5F, 1, 1, 6, 0.0F);
        setRotateAngle(tailTip, 0.11606439525762292F, 0.0F, 0.0F);
        hornL2B = new ModelRenderer(this, 30, 6);
        hornL2B.setRotationPoint(-0.08F, 0.7F, 0.28F);
        hornL2B.addBox(-0.5F, -3.9F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornL2B, -0.5235987755982988F, 1.3962634015954636F, 0.6981317007977318F);
        tailBase = new ModelRenderer(this, 28, 60);
        tailBase.setRotationPoint(0.0F, 3.0F, 11.8F);
        tailBase.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 7, 0.0F);
        setRotateAngle(tailBase, -1.3962634015954636F, 0.0F, 0.0F);
        tailHairE = new ModelRenderer(this, 30, 43);
        tailHairE.setRotationPoint(0.7F, -0.3F, 1.2F);
        tailHairE.addBox(0.0F, -1.0F, -0.5F, 0, 2, 4, 0.0F);
        setRotateAngle(tailHairE, 0.0F, 0.0F, 0.6981317007977318F);
        headBottom = new ModelRenderer(this, 2, 4);
        headBottom.setRotationPoint(0.0F, 2.15F, -0.45F);
        headBottom.addBox(-2.5F, -2.5F, -2.5F, 5, 8, 3, 0.0F);
        setRotateAngle(headBottom, -0.4363323129985824F, 0.0F, 0.0F);
        tailHairC = new ModelRenderer(this, 30, 45);
        tailHairC.setRotationPoint(0.0F, -1.2F, 0.2F);
        tailHairC.addBox(0.0F, -1.0F, -0.5F, 0, 2, 6, 0.0F);
        headFaceA = new ModelRenderer(this, 4, 15);
        headFaceA.setRotationPoint(-0.3F, -0.6F, -2.4F);
        headFaceA.addBox(-2.0F, -2.0F, -2.0F, 3, 8, 3, 0.0F);
        setRotateAngle(headFaceA, -0.13962634015954636F, 0.0F, 0.0F);
        hornR4 = new ModelRenderer(this, 30, 0);
        hornR4.setRotationPoint(0.85F, -2.3F, -0.1F);
        hornR4.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornR4, 0.08726646259971647F, 0.0F, 0.6981317007977318F);
        hornR2A = new ModelRenderer(this, 30, 6);
        hornR2A.setRotationPoint(-0.1F, 0.1F, 0.3F);
        hornR2A.addBox(-0.5F, -3.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornR2A, -0.5235987755982988F, -1.3962634015954636F, -0.6981317007977318F);
        hornRBase = new ModelRenderer(this, 26, 10);
        hornRBase.setRotationPoint(-1.3F, -2.7F, -1.5F);
        hornRBase.addBox(-1.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F);
        setRotateAngle(hornRBase, 1.7453292519943295F, 0.017453292519943295F, 0.17453292519943295F);
        hornL4 = new ModelRenderer(this, 30, 0);
        hornL4.mirror = true;
        hornL4.setRotationPoint(-0.85F, -2.3F, -0.1F);
        hornL4.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornL4, 0.08726646259971647F, 0.0F, -0.6981317007977318F);
        bodyMid = new ModelRenderer(this, 48, 41);
        bodyMid.setRotationPoint(0.0F, 5.8F, 2.3F);
        bodyMid.addBox(-4.0F, -5.0F, -3.0F, 8, 11, 7, -0.1F);
        body = new ModelRenderer(this, 46, 59);
        body.setRotationPoint(0.0F, 4.0F, -4.5F);
        body.addBox(-4.0F, -4.0F, -4.0F, 8, 12, 9, 0.0F);
        setRotateAngle(body, -0.13962634015954636F, 0.0F, 0.0F);
        tailHairD = new ModelRenderer(this, 30, 44);
        tailHairD.setRotationPoint(0.4F, -1.0F, 0.8F);
        tailHairD.addBox(0.0F, -1.0F, -0.5F, 0, 2, 5, 0.0F);
        setRotateAngle(tailHairD, 0.0F, 0.0F, 0.3490658503988659F);
        hornL2A = new ModelRenderer(this, 30, 6);
        hornL2A.mirror = true;
        hornL2A.setRotationPoint(-0.26F, 0.2F, 0.2F);
        hornL2A.addBox(-0.5F, -3.9F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(hornL2A, -0.5235987755982988F, 1.3962634015954636F, 0.6981317007977318F);
        tailHairB = new ModelRenderer(this, 30, 44);
        tailHairB.setRotationPoint(-0.4F, -1.0F, 0.8F);
        tailHairB.addBox(0.0F, -1.0F, -0.5F, 0, 2, 5, 0.0F);
        setRotateAngle(tailHairB, 0.0F, 0.0F, -0.3490658503988659F);
        rump = new ModelRenderer(this, 49, 23);
        rump.setRotationPoint(0.0F, 4.4F, 1.5F);
        rump.addBox(-3.5F, -4.0F, 3.5F, 7, 11, 7, 0.0F);
        setRotateAngle(rump, -0.08726646259971647F, 0.0F, 0.0F);
        hornR3 = new ModelRenderer(this, 30, 3);
        hornR3.setRotationPoint(0.2F, -3.5F, 0.25F);
        hornR3.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(hornR3, 0.08726646259971647F, 0.08726646259971647F, 0.6981317007977318F);
        headBase = new ModelRenderer(this, 0, 26);
        headBase.setRotationPoint(0.0F, -0.5F, -11.4F);
        headBase.addBox(-2.5F, -3.0F, -4.0F, 5, 3, 5, 0.1F);
        collar = new ModelRenderer(this, 50, 9);
        collar.setRotationPoint(0.0F, 3.0F, -7.7F);
        collar.addBox(-2.5F, -2.0F, -4.0F, 5, 6, 8, 0.0F);
        setRotateAngle(collar, 1.1519173063162573F, 0.0F, 0.0F);
        hornLBase = new ModelRenderer(this, 26, 10);
        hornLBase.setRotationPoint(1.3F, -2.7F, -1.5F);
        hornLBase.addBox(-3.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F);
        setRotateAngle(hornLBase, 1.7453292519943295F, -0.017453292519943295F, -0.17453292519943295F);
        noseB = new ModelRenderer(this, 5, 0);
        noseB.mirror = true;
        noseB.setRotationPoint(2.4F, 7.6F, -0.5F);
        noseB.addBox(-2.5F, -2.0F, -2.0F, 3, 2, 2, 0.0F);
        setRotateAngle(noseB, -0.3490658503988659F, 0.0F, 0.0F);
        earR = new ModelRenderer(this, 16, 0);
        earR.setRotationPoint(-2.0F, -1.1F, -1.0F);
        earR.addBox(-2.0F, -1.0F, -0.5F, 3, 2, 1, 0.0F);
        setRotateAngle(earR, 0.0F, -0.4363323129985824F, 0.3490658503988659F);
        earL = new ModelRenderer(this, 16, 0);
        earL.setRotationPoint(3.0F, -1.5F, -1.0F);
        earL.addBox(-2.0F, -1.0F, -0.5F, 3, 2, 1, 0.0F);
        setRotateAngle(earL, 0.0F, 0.4363323129985824F, -0.3490658503988659F);

        legLFront = new ModelRenderer(this, 0, 70);
        legLFront.mirror = true;
        legLFront.setRotationPoint(3.5F, 5.0F, -6.0F);
        legLFront.addBox(-1.0F, -0.5F, -1.5F, 2, 5, 4, 0.0F);
        setRotateAngle(legLFront, 0.3490658503988659F, 0.0F, -0.08726646259971647F);
        legLFrontMid = new ModelRenderer(this, 1, 60);
        legLFrontMid.mirror = true;
        legLFrontMid.setRotationPoint(0.4F, 3.0F, -0.6F);
        legLFrontMid.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 3, 0.0F);
        setRotateAngle(legLFrontMid, -0.3490658503988659F, 0.0F, 0.08726646259971647F);
        legLFrontBottom = new ModelRenderer(this, 2, 51);
        legLFrontBottom.mirror = true;
        legLFrontBottom.setRotationPoint(0.0F, 7.0F, 0.3F);
        legLFrontBottom.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        legLFrontAnkle = new ModelRenderer(this, 2, 48);
        legLFrontAnkle.mirror = true;
        legLFrontAnkle.setRotationPoint(0.01F, 5.4F, 0.8F);
        legLFrontAnkle.addBox(-1.5F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(legLFrontAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        legLFrontHoof = new ModelRenderer(this, 2, 45);
        legLFrontHoof.mirror = true;
        legLFrontHoof.setRotationPoint(-0.01F, 5.0F, -0.5F);
        legLFrontHoof.addBox(-1.5F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(legLFrontHoof, -1.1344640137963142F, 0.0F, 0.0F);
        thighLBack = new ModelRenderer(this, 13, 68);
        thighLBack.mirror = true;
        thighLBack.setRotationPoint(3.5F, 5.5F, 7.5F);
        thighLBack.addBox(-1.0F, -2.3F, -2.0F, 2, 7, 5, -0.0F);
        setRotateAngle(thighLBack, -0.17453292519943295F, 0.0F, -0.08726646259971647F);
        thighLBackMid = new ModelRenderer(this, 15, 59);
        thighLBackMid.mirror = true;
        thighLBackMid.setRotationPoint(-0.2F, 5.1F, -0.8F);
        thighLBackMid.addBox(-1.0F, -1.0F, 0.0F, 2, 6, 3, 0.0F);
        setRotateAngle(thighLBackMid, 0.5585053606381855F, 0.0F, 0.06981317007977318F);
        thighLBackBottom = new ModelRenderer(this, 16, 49);
        thighLBackBottom.mirror = true;
        thighLBackBottom.setRotationPoint(-0.05F, 5.0F, 0.0F);
        thighLBackBottom.addBox(-1.0F, -1.0F, 0.0F, 2, 8, 2, 0.0F);
        setRotateAngle(thighLBackBottom, -0.3839724354387525F, 0.0F, 0.017453292519943295F);
        thighLBackAnkle = new ModelRenderer(this, 16, 46);
        thighLBackAnkle.mirror = true;
        thighLBackAnkle.setRotationPoint(0.01F, 5.5F, 0.5F);
        thighLBackAnkle.addBox(-1.0F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(thighLBackAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        thighLBackHoof = new ModelRenderer(this, 16, 43);
        thighLBackHoof.mirror = true;
        thighLBackHoof.setRotationPoint(0.01F, 5.0F, -0.4F);
        thighLBackHoof.addBox(-1.0F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(thighLBackHoof, -1.1344640137963142F, 0.0F, 0.0F);

        legRFront = new ModelRenderer(this, 0, 70);
        legRFront.setRotationPoint(-3.5F, 5.0F, -6.0F);
        legRFront.addBox(-1.0F, -0.5F, -1.5F, 2, 5, 4, 0.0F);
        setRotateAngle(legRFront, 0.3490658503988659F, 0.0F, 0.08726646259971647F);
        legRFrontMid = new ModelRenderer(this, 1, 60);
        legRFrontMid.setRotationPoint(0.6F, 3.0F, -0.6F);
        legRFrontMid.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 3, 0.0F);
        setRotateAngle(legRFrontMid, -0.3490658503988659F, 0.0F, -0.08726646259971647F);
        legRFrontBottom = new ModelRenderer(this, 2, 51);
        legRFrontBottom.setRotationPoint(0.0F, 7.0F, 0.3F);
        legRFrontBottom.addBox(-1.5F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
        legRFrontAnkle = new ModelRenderer(this, 2, 48);
        legRFrontAnkle.setRotationPoint(0.01F, 5.4F, 0.8F);
        legRFrontAnkle.addBox(-1.5F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(legRFrontAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        legRFrontHoof = new ModelRenderer(this, 2, 45);
        legRFrontHoof.setRotationPoint(0.01F, 5.0F, -0.5F);
        legRFrontHoof.addBox(-1.5F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(legRFrontHoof, -1.1344640137963142F, 0.0F, 0.0F);
        thighRBack = new ModelRenderer(this, 13, 68);
        thighRBack.setRotationPoint(-3.5F, 5.5F, 7.5F);
        thighRBack.addBox(-1.0F, -2.3F, -2.0F, 2, 7, 5, -0.0F);
        setRotateAngle(thighRBack, -0.17453292519943295F, 0.0F, 0.08726646259971647F);
        thighRBackMid = new ModelRenderer(this, 15, 59);
        thighRBackMid.setRotationPoint(0.2F, 5.1F, -0.8F);
        thighRBackMid.addBox(-1.0F, -1.0F, 0.0F, 2, 6, 3, 0.0F);
        setRotateAngle(thighRBackMid, 0.5585053606381855F, 0.0F, -0.06981317007977318F);
        thighRBackBottom = new ModelRenderer(this, 16, 49);
        thighRBackBottom.setRotationPoint(-0.05F, 5.0F, 0.0F);
        thighRBackBottom.addBox(-1.0F, -1.0F, 0.0F, 2, 8, 2, 0.0F);
        setRotateAngle(thighRBackBottom, -0.3839724354387525F, 0.0F, -0.017453292519943295F);
        thighRBackAnkle = new ModelRenderer(this, 16, 46);
        thighRBackAnkle.setRotationPoint(0.01F, 5.5F, 0.5F);
        thighRBackAnkle.addBox(-1.0F, 0.4F, -3.0F, 2, 1, 2, 0.0F);
        setRotateAngle(thighRBackAnkle, 1.1344640137963142F, 0.0F, 0.0F);
        thighRBackHoof = new ModelRenderer(this, 16, 43);
        thighRBackHoof.setRotationPoint(0.01F, 5.0F, -0.4F);
        thighRBackHoof.addBox(-1.0F, 0.0F, -6.3F, 2, 1, 2, 0.0F);
        setRotateAngle(thighRBackHoof, -1.1344640137963142F, 0.0F, 0.0F);

        headBase.addChild(earR);
        headBase.addChild(earL);
        headBase.addChild(headFaceA);
        headBase.addChild(headFaceB);
        headFaceA.addChild(noseA);
        headFaceA.addChild(noseB);
        headBase.addChild(headBottom);

        headBase.addChild(hornRBase);
        hornRBase.addChild(hornR2A);
        hornRBase.addChild(hornR2B);
        hornR2A.addChild(hornR3);
        hornR3.addChild(hornR4);

        headBase.addChild(hornLBase);
        hornLBase.addChild(hornL2A);
        hornLBase.addChild(hornL2B);
        hornL2A.addChild(hornL3);
        hornL3.addChild(hornL4);

        tailBase.addChild(tailTip);
        tailTip.addChild(tailHairA);
        tailTip.addChild(tailHairB);
        tailTip.addChild(tailHairC);
        tailTip.addChild(tailHairD);
        tailTip.addChild(tailHairE);

        legLFront.addChild(legLFrontMid);
        legLFrontBottom.addChild(legLFrontAnkle);
        legLFrontMid.addChild(legLFrontBottom);
        legLFrontAnkle.addChild(legLFrontHoof);
        thighLBack.addChild(thighLBackMid);
        thighLBackMid.addChild(thighLBackBottom);
        thighLBackBottom.addChild(thighLBackAnkle);
        thighLBackAnkle.addChild(thighLBackHoof);
        legRFront.addChild(legRFrontMid);
        legRFrontBottom.addChild(legRFrontAnkle);
        legRFrontMid.addChild(legRFrontBottom);
        legRFrontAnkle.addChild(legRFrontHoof);
        thighRBack.addChild(thighRBackMid);
        thighRBackMid.addChild(thighRBackBottom);
        thighRBackBottom.addChild(thighRBackAnkle);
        thighRBackAnkle.addChild(thighRBackHoof);
    }

    @Override
    public void render(@Nonnull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        if (((EntityAnimal) entity).isChild())
        {
            double ageScale = 1;
            double percent = 1;
            if (entity instanceof IAnimalTFC)
            {
                percent = ((IAnimalTFC) entity).getPercentToAdulthood();
                ageScale = 1 / (2.0D - percent);
            }
            GlStateManager.scale(ageScale, ageScale, ageScale);
            GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0D, 1.0D, 1.0D);

        legLFront.render(scale);
        legRFront.render(scale);
        thighLBack.render(scale);
        thighRBack.render(scale);
        headBase.render(scale);
        neck.render(scale);
        collar.render(scale);
        body.render(scale);
        rump.render(scale);
        tailBase.render(scale);
        bodyMid.render(scale);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        {
            headBase.rotateAngleX = headPitch / (180F / (float) Math.PI);
            headBase.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);

            legRFront.rotateAngleX = MathHelper.cos(limbSwing * 0.4662F) * 0.8F * limbSwingAmount + 0.3490658503988659F;
            legLFront.rotateAngleX = MathHelper.cos(limbSwing * 0.4662F + (float) Math.PI) * 0.8F * limbSwingAmount + 0.3490658503988659F;
            thighRBack.rotateAngleX = MathHelper.cos(limbSwing * 0.4662F + (float) Math.PI) * 0.8F * limbSwingAmount + -0.17453292519943295F;
            thighLBack.rotateAngleX = MathHelper.cos(limbSwing * 0.4662F) * 0.8F * limbSwingAmount + -0.17453292519943295F;
        }
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}