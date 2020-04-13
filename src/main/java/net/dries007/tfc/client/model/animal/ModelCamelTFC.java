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
    public ModelRenderer bridleFront1;
    public ModelRenderer bridleBack1;
    public ModelRenderer bridleLeft1;
    public ModelRenderer bridleRight1;
    public ModelRenderer bridleFront2;
    public ModelRenderer bridleBack2;
    public ModelRenderer bridleLeft2;
    public ModelRenderer bridleRight2;
    public ModelRenderer bridleFrontTop1;
    public ModelRenderer bridleFrontBottom1;
    public ModelRenderer bridleFrontLeft1;
    public ModelRenderer bridleFrontRight1;
    public ModelRenderer bridleFrontTop2;
    public ModelRenderer bridleFrontBottom2;
    public ModelRenderer bridleFrontLeft2;
    public ModelRenderer bridleFrontRight2;
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

    public ModelCamelTFC()
    {

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
        blanketEdgeLeft = new ModelRenderer(this, 50, 85);
        blanketEdgeLeft.mirror = true;
        blanketEdgeLeft.setRotationPoint(5.5F, 3.5F, -2.0F);
        blanketEdgeLeft.addBox(0.0F, 0.0F, -8.2F, 1, 18, 19, 0.0F);
        strapFrontRight = new ModelRenderer(this, 0, 0);
        strapFrontRight.setRotationPoint(-5.2F, 5.45F, -9.0F);
        strapFrontRight.addBox(0.0F, -0.5F, -0.5F, 0, 7, 1, 0.0F);
        setRotation(strapFrontRight, -0.5235987755982988F, 0.0F, 0.0F);
        strapFrontLeftCurve = new ModelRenderer(this, 0, 0);
        strapFrontLeftCurve.setRotationPoint(5.22F, 10.65F, -12.6F);
        strapFrontLeftCurve.addBox(0.0F, 0.0F, 0.0F, 0, 6, 1, 0.0F);
        setRotation(strapFrontLeftCurve, -0.5410520681182421F, 0.15707963267948966F, 0.3665191429188092F);
        tail = new ModelRenderer(this, 26, 0);
        tail.setRotationPoint(-0.5F, 1.0F, 8.0F);
        tail.addBox(-0.5F, 0.0F, -0.5F, 1, 12, 1, 0.0F);
        setRotation(tail, 0.17976891295541594F, 0.0F, 0.0F);
        backLegRightMiddle = new ModelRenderer(this, 110, 14);
        backLegRightMiddle.setRotationPoint(0.0F, 11.0F, -0.5F);
        backLegRightMiddle.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F);
        bodyBack = new ModelRenderer(this, 12, 13);
        bodyBack.setRotationPoint(0.5F, -6.0F, 5.599999904632568F);
        bodyBack.addBox(-4.5F, 0.0F, 0.0F, 8, 11, 8, 0.0F);
        mandible = new ModelRenderer(this, 49, 9);
        mandible.setRotationPoint(0.0F, 2.0F, -2.2F);
        mandible.addBox(-1.0F, 0.0F, -3.0F, 2, 1, 3, 0.0F);
        setRotation(mandible, 0.17453292519943295F, 0.0F, 0.0F);
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
        blanketMiddle = new ModelRenderer(this, 9, 87);
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
        earLeft.setRotationPoint(1.6F, -3.0F, 1.0F);
        earLeft.addBox(-1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F);
        setRotation(earLeft, 0.0F, -0.3490658503988659F, 0.5235987755982988F);
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
        earRight.setRotationPoint(-1.6F, -3.0F, 1.0F);
        earRight.addBox(-1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F);
        setRotation(earRight, 0.0F, 0.3490658503988659F, -0.5235987755982988F);
        snout = new ModelRenderer(this, 46, 14);
        snout.setRotationPoint(0.0F, -2.5F, -3.0F);
        snout.addBox(-1.5F, 0.0F, -5.0F, 3, 3, 5, 0.0F);
        backLegLeftTop = new ModelRenderer(this, 109, 46);
        backLegLeftTop.setRotationPoint(4.2F, -2.5F, 10.0F);
        backLegLeftTop.addBox(-1.5F, -2.0F, -2.0F, 3, 13, 4, 0.0F);
        blanketEdgeRight = new ModelRenderer(this, 50, 85);
        blanketEdgeRight.setRotationPoint(-4.5F, 3.5F, -2.0F);
        blanketEdgeRight.addBox(-1.0F, 0.0F, -8.2F, 0, 18, 19, 0.0F);
        humpTop = new ModelRenderer(this, 73, 17);
        humpTop.setRotationPoint(0.0F, -9.8F, 0.0F);
        humpTop.addBox(-1.5F, -2.0F, -3.5F, 3, 2, 7, 0.0F);
        toesFrontLeft = new ModelRenderer(this, 110, 64);
        toesFrontLeft.setRotationPoint(0.0F, 8.0F, 0.0F);
        toesFrontLeft.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        frontLegRightBottom = new ModelRenderer(this, 112, 3);
        frontLegRightBottom.setRotationPoint(0.0F, 15.0F, 0.0F);
        frontLegRightBottom.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        bodyFront = new ModelRenderer(this, 1, 34);
        bodyFront.setRotationPoint(-5.0F, 2.0F, -12.0F);
        bodyFront.addBox(0.0F, -8.0F, 0.0F, 10, 13, 17, 0.0F);
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

        bridleFront1 = new ModelRenderer(this, 0, 0);
        bridleFront1.setRotationPoint(0.0F, 0.0F, -3.1F);
        bridleFront1.addBox(-2.1F, -0.5F, 0.0F, 4, 1, 0, 0.0F);
        bridleBack1 = new ModelRenderer(this, 0, 0);
        bridleBack1.setRotationPoint(0.2F, 0.0F, 2.01F);
        bridleBack1.addBox(-2.1F, -0.5F, 0.0F, 4, 1, 0, 0.0F);
        bridleLeft1 = new ModelRenderer(this, 0, 0);
        bridleLeft1.setRotationPoint(2.1F, 0.0F, -0.38F);
        bridleLeft1.addBox(0.0F, -0.5F, -2.6F, 0, 1, 5, 0.0F);
        bridleRight1 = new ModelRenderer(this, 0, 0);
        bridleRight1.setRotationPoint(-2.11F, 0.0F, -0.35F);
        bridleRight1.addBox(0.0F, -0.5F, -2.6F, 0, 1, 5, 0.0F);
        bridleFront2 = new ModelRenderer(this, 0, 0);
        bridleFront2.setRotationPoint(0.2F, 0.0F, -3.1F);
        bridleFront2.addBox(-2.1F, -0.5F, 0.0F, 4, 1, 0, 0.0F);
        bridleBack2 = new ModelRenderer(this, 0, 0);
        bridleBack2.setRotationPoint(0.0F, 0.0F, 2.03F);
        bridleBack2.addBox(-2.1F, -0.5F, 0.0F, 4, 1, 0, 0.0F);
        bridleLeft2 = new ModelRenderer(this, 0, 0);
        bridleLeft2.setRotationPoint(2.09F, 0.0F, -0.5F);
        bridleLeft2.addBox(0.0F, -0.5F, -2.6F, 0, 1, 5, 0.0F);
        bridleRight2 = new ModelRenderer(this, 0, 0);
        bridleRight2.setRotationPoint(-2.1F, 0.0F, -0.5F);
        bridleRight2.addBox(0.0F, -0.5F, -2.6F, 0, 1, 5, 0.0F);
        bridleFrontTop1 = new ModelRenderer(this, 0, 0);
        bridleFrontTop1.setRotationPoint(0.0F, -2.6F, -3.5F);
        bridleFrontTop1.addBox(-1.6F, 0.0F, -0.5F, 3, 0, 1, 0.0F);
        bridleFrontBottom1 = new ModelRenderer(this, 0, 0);
        bridleFrontBottom1.setRotationPoint(0.0F, 0.6F, -3.5F);
        bridleFrontBottom1.addBox(-1.6F, 0.0F, -0.5F, 3, 0, 1, 0.0F);
        bridleFrontLeft1 = new ModelRenderer(this, 0, 0);
        bridleFrontLeft1.setRotationPoint(0.0F, -2.4F, -3.5F);
        bridleFrontLeft1.addBox(1.6F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
        bridleFrontRight1 = new ModelRenderer(this, 0, 0);
        bridleFrontRight1.setRotationPoint(0.0F, -2.4F, -3.5F);
        bridleFrontRight1.addBox(-1.6F, 0.0F, -0.5F, 0, 3, 1, 0.0F);
        bridleFrontTop2 = new ModelRenderer(this, 0, 0);
        bridleFrontTop2.setRotationPoint(0.2F, -2.6F, -3.5F);
        bridleFrontTop2.addBox(-1.6F, 0.0F, -0.5F, 3, 0, 1, 0.0F);
        bridleFrontBottom2 = new ModelRenderer(this, 0, 0);
        bridleFrontBottom2.setRotationPoint(0.2F, 0.6F, -3.5F);
        bridleFrontBottom2.addBox(-1.6F, 0.0F, -0.5F, 3, 0, 1, 0.0F);
        bridleFrontLeft2 = new ModelRenderer(this, 0, 0);
        bridleFrontLeft2.setRotationPoint(0.01F, -2.4F, -3.5F);
        bridleFrontLeft2.addBox(1.6F, -0.2F, -0.5F, 0, 3, 1, 0.0F);
        bridleFrontRight2 = new ModelRenderer(this, 0, 0);
        bridleFrontRight2.setRotationPoint(0.0F, -2.6F, -3.5F);
        bridleFrontRight2.addBox(-1.6F, 0.0F, -0.5F, 0, 3, 1, 0.0F);

        saddle.addChild(strapFrontRightCurve);
        saddle.addChild(strapBellyBottom);
        saddle.addChild(strapBellyRight);
        backLegLeftBottom.addChild(toesBackLeft);
        frontLegRightBottom.addChild(toesFrontRight);
        blanketBase.addChild(blanketEdgeLeft);
        saddle.addChild(strapFrontRight);
        saddle.addChild(strapFrontLeftCurve);
        bodyBack.addChild(tail);
        backLegRightTop.addChild(backLegRightMiddle);
        snout.addChild(mandible);
        backLegLeftMiddle.addChild(backLegLeftBottom);
        saddle.addChild(saddlePostFront);
        blanketBase.addChild(blanketTop);
        saddle.addChild(strapChestLeft);
        backLegLeftTop.addChild(backLegLeftMiddle);
        backLegRightMiddle.addChild(backLegRightBottom);
        saddle.addChild(strapNeckBottom);
        blanketBase.addChild(blanketMiddle);
        neckBase.addChild(neckUpper);
        head.addChild(earLeft);
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
        neckUpper.addChild(head);
        this.head.addChild(this.bridleLeft2);
        this.head.addChild(this.bridleFront2);
        this.head.addChild(this.bridleBack2);
        this.head.addChild(this.bridleFront1);
        this.head.addChild(this.bridleFrontLeft1);
        this.head.addChild(this.bridleBack1);
        this.head.addChild(this.bridleRight1);
        this.head.addChild(this.bridleFrontBottom2);
        this.head.addChild(this.bridleRight2);
        this.head.addChild(this.bridleFrontRight1);
        this.head.addChild(this.bridleFrontBottom1);
        this.head.addChild(this.bridleLeft1);
        this.head.addChild(this.bridleFrontTop1);
        this.head.addChild(this.bridleFrontTop2);
        this.head.addChild(this.bridleFrontLeft2);
        this.head.addChild(this.bridleFrontRight2);
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

        bridleFront1.isHidden = true;
        bridleFront2.isHidden = true;
        bridleBack1.isHidden = true;
        bridleBack2.isHidden = true;
        bridleLeft1.isHidden = true;
        bridleLeft2.isHidden = true;
        bridleRight1.isHidden = true;
        bridleRight2.isHidden = true;


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

        /*float f12 = 0.2617994F * f6;
        float f13 = MathHelper.cos(f9 * 0.6F + 3.1415927F);
        this.frontLeftLeg.rotationPointY = -2.0F * f6 + 9.0F * f7;
        this.frontLeftLeg.rotationPointZ = -2.0F * f6 + -8.0F * f7;
        this.frontRightLeg.rotationPointY = this.frontLeftLeg.rotationPointY;
        this.frontRightLeg.rotationPointZ = this.frontLeftLeg.rotationPointZ;
        this.backLeftShin.rotationPointY = this.backLeftLeg.rotationPointY + MathHelper.sin(1.5707964F + f12 + f7 * -f10 * 0.5F * limbSwingAmount) * 7.0F;
        this.backLeftShin.rotationPointZ = this.backLeftLeg.rotationPointZ + MathHelper.cos(-1.5707964F + f12 + f7 * -f10 * 0.5F * limbSwingAmount) * 7.0F;
        this.backRightShin.rotationPointY = this.backRightLeg.rotationPointY + MathHelper.sin(1.5707964F + f12 + f7 * f10 * 0.5F * limbSwingAmount) * 7.0F;
        this.backRightShin.rotationPointZ = this.backRightLeg.rotationPointZ + MathHelper.cos(-1.5707964F + f12 + f7 * f10 * 0.5F * limbSwingAmount) * 7.0F;
        float f14 = (-1.0471976F + f13) * f6 + f11 * f7;
        float f15 = (-1.0471976F - f13) * f6 + -f11 * f7;
        this.frontLeftShin.rotationPointY = this.frontLeftLeg.rotationPointY + MathHelper.sin(1.5707964F + f14) * 7.0F;
        this.frontLeftShin.rotationPointZ = this.frontLeftLeg.rotationPointZ + MathHelper.cos(-1.5707964F + f14) * 7.0F;
        this.frontRightShin.rotationPointY = this.frontRightLeg.rotationPointY + MathHelper.sin(1.5707964F + f15) * 7.0F;
        this.frontRightShin.rotationPointZ = this.frontRightLeg.rotationPointZ + MathHelper.cos(-1.5707964F + f15) * 7.0F;
        this.backLeftLeg.rotateAngleX = f12 + -f10 * 0.5F * limbSwingAmount * f7;
        this.backLeftShin.rotateAngleX = -0.08726646F * f6 + (-f10 * 0.5F * limbSwingAmount - Math.max(0.0F, f10 * 0.5F * limbSwingAmount)) * f7;
        this.backLeftHoof.rotateAngleX = this.backLeftShin.rotateAngleX;
        this.backRightLeg.rotateAngleX = f12 + f10 * 0.5F * limbSwingAmount * f7;
        this.backRightShin.rotateAngleX = -0.08726646F * f6 + (f10 * 0.5F * limbSwingAmount - Math.max(0.0F, -f10 * 0.5F * limbSwingAmount)) * f7;
        this.backRightHoof.rotateAngleX = this.backRightShin.rotateAngleX;
        this.frontLeftLeg.rotateAngleX = f14;
        this.frontLeftShin.rotateAngleX = (this.frontLeftLeg.rotateAngleX + 3.1415927F * Math.max(0.0F, 0.2F + f13 * 0.2F)) * f6 + (f11 + Math.max(0.0F, f10 * 0.5F * limbSwingAmount)) * f7;
        this.frontLeftHoof.rotateAngleX = this.frontLeftShin.rotateAngleX;
        this.frontRightLeg.rotateAngleX = f15;
        this.frontRightShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + 3.1415927F * Math.max(0.0F, 0.2F - f13 * 0.2F)) * f6 + (-f11 + Math.max(0.0F, -f10 * 0.5F * limbSwingAmount)) * f7;
        this.frontRightHoof.rotateAngleX = this.frontRightShin.rotateAngleX;
        this.backLeftHoof.rotationPointY = this.backLeftShin.rotationPointY;
        this.backLeftHoof.rotationPointZ = this.backLeftShin.rotationPointZ;
        this.backRightHoof.rotationPointY = this.backRightShin.rotationPointY;
        this.backRightHoof.rotationPointZ = this.backRightShin.rotationPointZ;
        this.frontLeftHoof.rotationPointY = this.frontLeftShin.rotationPointY;
        this.frontLeftHoof.rotationPointZ = this.frontLeftShin.rotationPointZ;
        this.frontRightHoof.rotationPointY = this.frontRightShin.rotationPointY;
        this.frontRightHoof.rotationPointZ = this.frontRightShin.rotationPointZ;
        */
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
