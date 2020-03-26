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
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.IAnimalTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelCamelTFC extends ModelBase
{
    public ModelRenderer saddle;
    public ModelRenderer blanketBase;
    public ModelRenderer frontLegLeftTop;
    public ModelRenderer humpBottom;
    public ModelRenderer humpTop;
    public ModelRenderer frontLegRightTop;
    public ModelRenderer humpMiddle;
    public ModelRenderer neckBase;
    public ModelRenderer bodyBack;
    public ModelRenderer tail;
    public ModelRenderer backLegLeftTop;
    public ModelRenderer bodyFront;
    public ModelRenderer backLegRightTop;
    public ModelRenderer saddlePostBack;
    public ModelRenderer saddlePostFront;
    public ModelRenderer bridleFront;
    public ModelRenderer bridleFrontRight;
    public ModelRenderer bridleRear;
    public ModelRenderer bridleFrontTop;
    public ModelRenderer bridleFrontBottom;
    public ModelRenderer bridleLeft;
    public ModelRenderer bridleRight;
    public ModelRenderer bridleFrontLeft;
    public ModelRenderer strapChestLeft;
    public ModelRenderer strapBellyRight;
    public ModelRenderer strapBellyLeft;
    public ModelRenderer strapFrontRight;
    public ModelRenderer strapNeckBottom;
    public ModelRenderer strapBellyBottom;
    public ModelRenderer strapFrontLeft;
    public ModelRenderer strapChestRight;
    public ModelRenderer strapChestBottom;
    public ModelRenderer strapFrontLeftCurve;
    public ModelRenderer strapFrontRightCurve;
    public ModelRenderer blanketMiddle;
    public ModelRenderer blanketEdgeRight;
    public ModelRenderer blanketEdgeLeft;
    public ModelRenderer blanketTop;
    public ModelRenderer frontLegLeftBottom;
    public ModelRenderer toesFrontLeft;
    public ModelRenderer frontLegRightBottom;
    public ModelRenderer toesFrontRight;
    public ModelRenderer neckUpper;
    public ModelRenderer head;
    public ModelRenderer earRight;
    public ModelRenderer snout;
    public ModelRenderer earLeft;
    public ModelRenderer mandible;
    public ModelRenderer backLegLeftMiddle;
    public ModelRenderer backLegLeftBottom;
    public ModelRenderer toesBackLeft;
    public ModelRenderer backLegRightMiddle;
    public ModelRenderer backLegRightBottom;
    public ModelRenderer toesBackRight;
    public ModelRenderer chestLeft;
    public ModelRenderer chestRight;

    public ModelCamelTFC() {

        textureWidth = 128;
        textureHeight = 128;

        strapFrontRightCurve = new ModelRenderer(this, 0, 0);
        strapFrontRightCurve.setRotationPoint(-5.22F, 10.65F, -12.6F);
        strapFrontRightCurve.addBox(0.0F, 0.0F, 0.0F, 0, 6, 1, 0.0F);
        setRotation(strapFrontRightCurve, -0.5410520681182421F, -0.15707963267948966F, -0.3665191429188092F);
        frontLegRightTop = new ModelRenderer(this, 110, 24);
        frontLegRightTop.setRotationPoint(-4.6F, -1.0F, -8.0F);
        frontLegRightTop.addBox(-1.5F, -2.0F, -1.5F, 3, 17, 3, 0.0F);
        humpMiddle = new ModelRenderer(this, 66, 27);
        humpMiddle.setRotationPoint(0.0F, -7.0F, 0.0F);
        humpMiddle.addBox(-2.5F, -3.0F, -6.0F, 5, 3, 12, 0.0F);
        strapBellyBottom = new ModelRenderer(this, 0, 0);
        strapBellyBottom.setRotationPoint(5.2F, 18.1F, 6.7F);
        strapBellyBottom.addBox(-9.2F, 0.0F, -0.5F, 8, 0, 1, 0.0F);
        blanketBase = new ModelRenderer(this, 0, 105);
        blanketBase.setRotationPoint(0.0F, -7.8F, 1.0F);
        blanketBase.addBox(-5.5F, -0.5F, -10.2F, 11, 4, 19, 0.0F);
        strapBellyRight = new ModelRenderer(this, 0, 0);
        strapBellyRight.setRotationPoint(-4.01F, 8.1F, 6.7F);
        strapBellyRight.addBox(0.0F, 0.0F, -0.5F, 0, 10, 1, 0.0F);
        chestRight = new ModelRenderer(this, 95, 89);
        chestRight.mirror = true;
        chestRight.setRotationPoint(-8.0F, -5.5F, 2.5F);
        chestRight.addBox(0.0F, 0.0F, -4.0F, 3, 8, 8, 0.0F);
        setRotation(chestRight, 0.0F, 0.0F, -0.03490658503988659F);
        toesBackLeft = new ModelRenderer(this, 110, 64);
        toesBackLeft.setRotationPoint(0.0F, 7.800000190734863F, 0.0F);
        toesBackLeft.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotation(toesBackLeft, 0.17453292012214658F, 0.0F, 0.0F);
        toesFrontRight = new ModelRenderer(this, 110, 64);
        toesFrontRight.setRotationPoint(0.0F, 8.0F, 0.0F);
        toesFrontRight.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        humpBottom = new ModelRenderer(this, 58, 43);
        humpBottom.setRotationPoint(0.0F, -6.0F, 0.0F);
        humpBottom.addBox(-3.5F, -2.0F, -9.0F, 7, 2, 18, 0.0F);
        blanketEdgeLeft = new ModelRenderer(this, 44, 101);
        blanketEdgeLeft.setRotationPoint(4.5F, 3.5F, 0.0F);
        blanketEdgeLeft.addBox(0.0F, 0.0F, -8.2F, 1, 5, 15, 0.0F);
        strapFrontRight = new ModelRenderer(this, 0, 0);
        strapFrontRight.setRotationPoint(-5.2F, 5.45F, -9.0F);
        strapFrontRight.addBox(0.0F, -0.5F, -0.5F, 0, 7, 1, 0.0F);
        setRotation(strapFrontRight, -0.5235987755982988F, 0.0F, 0.0F);
        strapFrontLeftCurve = new ModelRenderer(this, 0, 0);
        strapFrontLeftCurve.setRotationPoint(5.22F, 10.65F, -12.6F);
        strapFrontLeftCurve.addBox(0.0F, 0.0F, 0.0F, 0, 6, 1, 0.0F);
        setRotation(strapFrontLeftCurve, -0.5410520681182421F, 0.15707963267948966F, 0.3665191429188092F);
        tail = new ModelRenderer(this, 26, 0);
        tail.setRotationPoint(-0.5F, 1.0F, 7.0F);
        tail.addBox(-0.5F, 0.0F, -0.5F, 1, 12, 1, 0.0F);
        setRotation(tail, 0.17976891295541594F, 0.0F, 0.0F);
        backLegRightMiddle = new ModelRenderer(this, 110, 14);
        backLegRightMiddle.setRotationPoint(0.0F, 11.0F, -0.5F);
        backLegRightMiddle.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F);
        bodyBack = new ModelRenderer(this, 13, 14);
        bodyBack.setRotationPoint(0.5F, -6.0F, 5.599999904632568F);
        bodyBack.addBox(-4.5F, 0.0F, 0.0F, 8, 11, 7, 0.0F);
        mandible = new ModelRenderer(this, 49, 9);
        mandible.setRotationPoint(0.0F, 2.0F, -2.5F);
        mandible.addBox(-1.0F, 0.0F, -3.0F, 2, 1, 3, 0.0F);
        setRotation(mandible, 0.34906584024429316F, 0.0F, 0.0F);
        chestLeft = new ModelRenderer(this, 95, 89);
        chestLeft.setRotationPoint(5.0F, -5.5F, 2.5F);
        chestLeft.addBox(0.0F, 0.0F, -4.0F, 3, 8, 8, 0.0F);
        setRotation(chestLeft, 0.0F, 0.0F, 0.03490658503988659F);
        backLegLeftBottom = new ModelRenderer(this, 112, 3);
        backLegLeftBottom.setRotationPoint(0.0F, 5.800000190734863F, 0.0F);
        backLegLeftBottom.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        setRotation(backLegLeftBottom, -0.17453292012214658F, 0.0F, 0.0F);
        saddlePostFront = new ModelRenderer(this, 0, 0);
        saddlePostFront.setRotationPoint(0.0F, 0.0F, -6.0F);
        saddlePostFront.addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1, 0.0F);
        blanketTop = new ModelRenderer(this, 18, 75);
        blanketTop.setRotationPoint(0.0F, -2.5F, -0.5F);
        blanketTop.addBox(-2.0F, -2.0F, -4.2F, 4, 3, 8, 0.0F);
        strapChestLeft = new ModelRenderer(this, 0, 0);
        strapChestLeft.setRotationPoint(5.01F, 8.1F, -6.0F);
        strapChestLeft.addBox(0.0F, 0.0F, -0.5F, 0, 12, 1, 0.0F);
        backLegRightTop = new ModelRenderer(this, 109, 46);
        backLegRightTop.setRotationPoint(-4.2F, -2.5F, 10.0F);
        backLegRightTop.addBox(-1.5F, -2.0F, -2.0F, 3, 13, 4, 0.0F);
        backLegLeftMiddle = new ModelRenderer(this, 110, 14);
        backLegLeftMiddle.setRotationPoint(0.0F, 11.0F, -0.5F);
        backLegLeftMiddle.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F);
        frontLegLeftTop = new ModelRenderer(this, 110, 24);
        frontLegLeftTop.setRotationPoint(4.6F, -1.0F, -8.0F);
        frontLegLeftTop.addBox(-1.5F, -2.0F, -1.5F, 3, 17, 3, 0.0F);
        backLegRightBottom = new ModelRenderer(this, 112, 3);
        backLegRightBottom.setRotationPoint(0.0F, 5.800000190734863F, 0.0F);
        backLegRightBottom.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        setRotation(backLegRightBottom, -0.17453292012214658F, 0.0F, 0.0F);
        strapNeckBottom = new ModelRenderer(this, 0, 0);
        strapNeckBottom.setRotationPoint(6.2F, 15.5F, -15.2F);
        strapNeckBottom.addBox(-9.2F, 0.0F, -0.5F, 6, 0, 1, 0.0F);
        setRotation(strapNeckBottom, -0.5585053606381855F, 0.0F, 0.0F);
        blanketMiddle = new ModelRenderer(this, 12, 87);
        blanketMiddle.setRotationPoint(0.0F, 0.0F, 2.0F);
        blanketMiddle.addBox(-4.0F, -2.5F, -9.2F, 8, 3, 13, 0.0F);
        neckUpper = new ModelRenderer(this, 2, 33);
        neckUpper.setRotationPoint(0.0F, 0.0F, -5.400000095367432F);
        neckUpper.addBox(-1.5F, -12.0F, -3.0F, 3, 13, 4, 0.0F);
        setRotation(neckUpper, 0.2617993950843811F, 0.0F, 0.0F);
        saddle = new ModelRenderer(this, 84, 108);
        saddle.setRotationPoint(0.0F, -13.0F, 0.0F);
        saddle.addBox(-3.5F, 0.5F, -7.2F, 7, 5, 15, 0.0F);
        earLeft = new ModelRenderer(this, 51, 5);
        earLeft.setRotationPoint(2.0F, -3.0F, 1.0F);
        earLeft.addBox(-1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F);
        setRotation(earLeft, 0.0F, -0.34906584024429316F, 0.0F);
        frontLegLeftBottom = new ModelRenderer(this, 112, 3);
        frontLegLeftBottom.setRotationPoint(0.0F, 15.0F, 0.0F);
        frontLegLeftBottom.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        strapChestBottom = new ModelRenderer(this, 0, 0);
        strapChestBottom.setRotationPoint(4.2F, 20.1F, -6.0F);
        strapChestBottom.addBox(-9.2F, 0.0F, -0.5F, 10, 0, 1, 0.0F);
        strapChestRight = new ModelRenderer(this, 0, 0);
        strapChestRight.setRotationPoint(-5.01F, 8.1F, -6.0F);
        strapChestRight.addBox(0.0F, 0.0F, -0.5F, 0, 12, 1, 0.0F);
        strapBellyLeft = new ModelRenderer(this, 0, 0);
        strapBellyLeft.setRotationPoint(4.01F, 8.1F, 6.7F);
        strapBellyLeft.addBox(0.0F, 0.0F, -0.5F, 0, 10, 1, 0.0F);
        strapFrontLeft = new ModelRenderer(this, 0, 0);
        strapFrontLeft.setRotationPoint(5.2F, 5.45F, -9.0F);
        strapFrontLeft.addBox(0.0F, -0.5F, -0.5F, 0, 7, 1, 0.0F);
        setRotation(strapFrontLeft, -0.5235987755982988F, 0.0F, 0.0F);
        saddlePostBack = new ModelRenderer(this, 0, 0);
        saddlePostBack.setRotationPoint(0.0F, 0.0F, 6.5F);
        saddlePostBack.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        earRight = new ModelRenderer(this, 51, 5);
        earRight.mirror = true;
        earRight.setRotationPoint(-2.0F, -3.0F, 1.0F);
        earRight.addBox(-1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F);
        setRotation(earRight, 0.0F, 0.34906584024429316F, 0.0F);
        snout = new ModelRenderer(this, 46, 14);
        snout.setRotationPoint(0.0F, -2.5F, -3.0F);
        snout.addBox(-1.5F, 0.0F, -5.0F, 3, 3, 5, 0.0F);
        backLegLeftTop = new ModelRenderer(this, 109, 46);
        backLegLeftTop.setRotationPoint(4.2F, -2.5F, 10.0F);
        backLegLeftTop.addBox(-1.5F, -2.0F, -2.0F, 3, 13, 4, 0.0F);
        blanketEdgeRight = new ModelRenderer(this, 44, 101);
        blanketEdgeRight.setRotationPoint(-4.5F, 3.5F, 0.0F);
        blanketEdgeRight.addBox(-1.0F, 0.0F, -8.2F, 1, 5, 15, 0.0F);
        humpTop = new ModelRenderer(this, 73, 17);
        humpTop.setRotationPoint(0.0F, -9.8F, 0.0F);
        humpTop.addBox(-1.5F, -2.0F, -3.5F, 3, 2, 7, 0.0F);
        toesFrontLeft = new ModelRenderer(this, 110, 64);
        toesFrontLeft.setRotationPoint(0.0F, 8.0F, 0.0F);
        toesFrontLeft.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        frontLegRightBottom = new ModelRenderer(this, 112, 3);
        frontLegRightBottom.setRotationPoint(0.0F, 15.0F, 0.0F);
        frontLegRightBottom.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        bodyFront = new ModelRenderer(this, 0, 33);
        bodyFront.setRotationPoint(-5.0F, 2.0F, -12.0F);
        bodyFront.addBox(0.0F, -8.0F, 0.0F, 10, 13, 18, 0.0F);
        toesBackRight = new ModelRenderer(this, 110, 64);
        toesBackRight.setRotationPoint(0.0F, 7.800000190734863F, 0.0F);
        toesBackRight.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotation(toesBackRight, 0.17453292012214658F, 0.0F, 0.0F);
        neckBase = new ModelRenderer(this, 40, 34);
        neckBase.setRotationPoint(0.0F, 0.0F, -12.0F);
        neckBase.addBox(-2.5F, -3.5F, -8.0F, 5, 6, 9, 0.0F);
        setRotation(neckBase, -0.10471975803375246F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 45, 23);
        head.setRotationPoint(0.0F, -12.0F, -0.3F);
        head.addBox(-2.0F, -3.0F, -3.0F, 4, 4, 5, 0.0F);


        bridleFrontLeft = new ModelRenderer(this, 0, 0);
        bridleFrontLeft.setRotationPoint(0.0F, -2.6F, -3.5F);
        bridleFrontLeft.addBox(-1.6F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
        bridleFront = new ModelRenderer(this, 0, 0);
        bridleFront.setRotationPoint(0.0F, 0.0F, -3.1F);
        bridleFront.addBox(-2.1F, -0.5F, 0.0F, 4, 1, 1, 0.0F);
        bridleFrontTop = new ModelRenderer(this, 0, 0);
        bridleFrontTop.setRotationPoint(0.0F, -2.6F, -3.5F);
        bridleFrontTop.addBox(-1.6F, 0.0F, -0.5F, 3, 0, 1, 0.0F);
        bridleFrontRight = new ModelRenderer(this, 0, 0);
        bridleFrontRight.setRotationPoint(0.0F, -2.6F, -3.5F);
        bridleFrontRight.addBox(1.6F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
        bridleRear = new ModelRenderer(this, 0, 0);
        bridleRear.setRotationPoint(0.0F, 0.0F, 2.1F);
        bridleRear.addBox(-2.1F, -0.5F, 0.0F, 4, 1, 0, 0.0F);
        bridleLeft = new ModelRenderer(this, 0, 0);
        bridleLeft.setRotationPoint(2.1F, 0.0F, -0.5F);
        bridleLeft.addBox(0.0F, -0.5F, -2.6F, 0, 1, 5, 0.0F);
        bridleRight = new ModelRenderer(this, 0, 0);
        bridleRight.setRotationPoint(-2.1F, 0.0F, -0.5F);
        bridleRight.addBox(0.0F, -0.5F, -2.6F, 0, 1, 5, 0.0F);
        bridleFrontBottom = new ModelRenderer(this, 0, 0);
        bridleFrontBottom.setRotationPoint(0.0F, -2.6F, -3.5F);
        bridleFrontBottom.addBox(-1.6F, 0.0F, -0.5F, 3, 0, 1, 0.0F);

        saddle.addChild(strapFrontRightCurve);
        saddle.addChild(strapBellyBottom);
        saddle.addChild(strapBellyRight);
        backLegLeftBottom.addChild(toesBackLeft);
        frontLegRightBottom.addChild(toesFrontRight);
        saddle.addChild(bridleFrontBottom);
        blanketBase.addChild(blanketEdgeLeft);
        saddle.addChild(strapFrontRight);
        saddle.addChild(strapFrontLeftCurve);
        bodyBack.addChild(tail);
        backLegRightTop.addChild(backLegRightMiddle);
        saddle.addChild(bridleLeft);
        saddle.addChild(bridleRight);
        snout.addChild(mandible);
        backLegLeftMiddle.addChild(backLegLeftBottom);
        saddle.addChild(saddlePostFront);
        blanketBase.addChild(blanketTop);
        saddle.addChild(strapChestLeft);
        backLegLeftTop.addChild(backLegLeftMiddle);
        saddle.addChild(bridleRear);
        backLegRightMiddle.addChild(backLegRightBottom);
        saddle.addChild(strapNeckBottom);
        blanketBase.addChild(blanketMiddle);
        neckBase.addChild(neckUpper);
        head.addChild(earLeft);
        saddle.addChild(bridleFrontTop);
        saddle.addChild(bridleFrontLeft);
        frontLegLeftTop.addChild(frontLegLeftBottom);
        saddle.addChild(strapChestBottom);
        saddle.addChild(strapChestRight);
        saddle.addChild(strapBellyLeft);
        saddle.addChild(strapFrontLeft);
        saddle.addChild(saddlePostBack);
        head.addChild(earRight);
        head.addChild(snout);
        blanketBase.addChild(blanketEdgeRight);
        frontLegLeftBottom.addChild(toesFrontLeft);
        frontLegRightTop.addChild(frontLegRightBottom);
        backLegRightBottom.addChild(toesBackRight);
        saddle.addChild(bridleFrontRight);
        saddle.addChild(bridleFront);
        neckUpper.addChild(head);
    }




    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        AbstractChestHorse abstractchesthorse = (AbstractChestHorse) entityIn;
        boolean flag1 = !abstractchesthorse.isChild() && abstractchesthorse.hasChest();
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        if (((EntityAnimal) entityIn).isChild())
        {
            double ageScale = 1;
            double percent = 1;
            if (entityIn instanceof IAnimalTFC)
            {
                percent = ((IAnimalTFC) entityIn).getPercentToAdulthood();
                ageScale = 1 / (2.0D - percent);
            }
            GlStateManager.scale(ageScale, ageScale, ageScale);
            GlStateManager.translate(0.0F, 1.5f - (1.5f * percent), 0f);
        }

        neckBase.render(scale);
        frontLegLeftTop.render(scale);
        frontLegRightTop.render(scale);
        backLegLeftTop.render(scale);
        backLegRightTop.render(scale);
        bodyBack.render(scale);
        bodyFront.render(scale);
        humpBottom.render(scale);
        humpMiddle.render(scale);
        humpTop.render(scale);

        if (flag1)
        {
            this.blanketBase.render(scale);
            this.chestLeft.render(scale);
            this.chestRight.render(scale);
        }

    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {

        this.head.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.frontLegRightTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.frontLegLeftTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.backLegLeftTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        this.backLegRightTop.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
