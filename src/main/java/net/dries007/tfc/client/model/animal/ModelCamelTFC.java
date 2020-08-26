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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityCamelTFC;

/**
 * ModelCamelTFC
 * Created using Tabula 7.1.0
 */

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ModelCamelTFC extends ModelBase
{
    public ModelRenderer saddle;
    public ModelRenderer frontLegLeftTop;
    public ModelRenderer humpBottom;
    public ModelRenderer frontLegRightTop;
    public ModelRenderer humpTop;
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
    public ModelRenderer strapBellyBottom;
    public ModelRenderer strapChestRight;
    public ModelRenderer strapChestBottom;
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
    public ModelRenderer frontLegLeftMiddle;
    public ModelRenderer frontLegRightMiddle;
    public ModelRenderer reinsRight;
    public ModelRenderer reinsLeft;
    public ModelRenderer strapChestLeftAngle;
    public ModelRenderer strapChestRightAngle;
    public ModelRenderer strapBellyLeftAngle;
    public ModelRenderer strapBellyRightAngle;
    public ModelRenderer headNode;

    public ModelCamelTFC(float scale)
    {
        textureWidth = 128;
        textureHeight = 80;

        strapChestRight = new ModelRenderer(this, 0, 0);
        strapChestRight.setRotationPoint(-5.48F, 6.5F, -6.0F);
        strapChestRight.addBox(0.0F, 0.0F, 0.0F, 0, 13, 1, 0.0F);
        strapChestLeft = new ModelRenderer(this, 0, 0);
        strapChestLeft.setRotationPoint(5.48F, 6.5F, -6.0F);
        strapChestLeft.addBox(0.0F, 0.0F, 0.0F, 0, 13, 1, 0.0F);
        strapChestBottom = new ModelRenderer(this, 0, 0);
        strapChestBottom.setRotationPoint(3.5F, 19.5F, -6.0F);
        strapChestBottom.addBox(-9.0F, 0.0F, 0.0F, 11, 0, 1, 0.0F);
        strapBellyRight = new ModelRenderer(this, 0, 0);
        strapBellyRight.setRotationPoint(-4.48F, 6.5F, 5.5F);
        strapBellyRight.addBox(0.0F, 0.0F, 0.0F, 0, 11, 1, 0.0F);
        strapBellyLeft = new ModelRenderer(this, 0, 0);
        strapBellyLeft.setRotationPoint(4.48F, 6.5F, 5.5F);
        strapBellyLeft.addBox(0.0F, 0.0F, 0.0F, 0, 11, 1, 0.0F);
        strapBellyBottom = new ModelRenderer(this, 0, 0);
        strapBellyBottom.setRotationPoint(4.5F, 17.5F, 5.5F);
        strapBellyBottom.addBox(-9.0F, 0.0F, 0.0F, 9, 0, 1, 0.0F);

        headNode = new ModelRenderer(this, 40, 34);
        headNode.setRotationPoint(0.0F, 0.5F, -10.0F);
        headNode.addBox(-2.5F, -3.5F, -8.0F, 1, 1, 1, 0.0F);
        tail = new ModelRenderer(this, 26, 0);
        tail.setRotationPoint(-0.5F, 1.5F, 8.0F);
        tail.addBox(-0.5F, 0.0F, -0.5F, 1, 12, 1, 0.0F);
        setRotation(tail, 0.17976891295541594F, 0.0F, 0.0F);
        mandible = new ModelRenderer(this, 49, 9);
        mandible.setRotationPoint(0.0F, 2.0F, -2.2F);
        mandible.addBox(-1.0F, 0.0F, -3.0F, 2, 1, 3, 0.0F);
        setRotation(mandible, 0.17453292519943295F, 0.0F, 0.0F);
        neckUpper = new ModelRenderer(this, 2, 33);
        neckUpper.setRotationPoint(0.0F, 0.0F, -5.4F);
        neckUpper.addBox(-1.5F, -12.0F, -3.0F, 3, 13, 4, 0.0F);
        setRotation(neckUpper, 0.2617993950843811F, 0.0F, 0.0F);
        earLeft = new ModelRenderer(this, 51, 5);
        earLeft.setRotationPoint(1.6F, -3.0F, 1.0F);
        earLeft.addBox(-1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F);
        setRotation(earLeft, 0.0F, -0.3490658503988659F, 0.5235987755982988F);
        earRight = new ModelRenderer(this, 51, 5);
        earRight.mirror = true;
        earRight.setRotationPoint(-1.6F, -3.0F, 1.0F);
        earRight.addBox(-1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F);
        setRotation(earRight, 0.0F, 0.3490658503988659F, -0.5235987755982988F);
        snout = new ModelRenderer(this, 46, 14);
        snout.setRotationPoint(0.0F, -2.5F, -3.0F);
        snout.addBox(-1.5F, 0.0F, -5.0F, 3, 3, 5, 0.0F);
        toesFrontLeft = new ModelRenderer(this, 110, 64);
        toesFrontLeft.setRotationPoint(0.0F, 8.0F, 0.0F);
        toesFrontLeft.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        toesBackRight = new ModelRenderer(this, 110, 64);
        toesBackRight.setRotationPoint(0.0F, 7.8F, 0.0F);
        toesBackRight.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        toesBackLeft = new ModelRenderer(this, 110, 64);
        toesBackLeft.setRotationPoint(0.0F, 7.8F, 0.0F);
        toesBackLeft.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        toesFrontRight = new ModelRenderer(this, 110, 64);
        toesFrontRight.setRotationPoint(0.0F, 8.0F, 0.0F);
        toesFrontRight.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 3, 0.0F);
        neckBase = new ModelRenderer(this, 40, 34);
        neckBase.setRotationPoint(0.0F, 0.5F, -10.0F);
        neckBase.addBox(-2.5F, -3.5F, -8.0F, 5, 6, 9, 0.0F);
        setRotation(neckBase, -0.10471975803375246F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 45, 23);
        head.setRotationPoint(0.0F, -12.0F, -0.3F);
        head.addBox(-2.0F, -3.0F, -3.0F, 4, 4, 5, 0.0F);

        frontLegLeftTop = new ModelRenderer(this, 110, 38);
        frontLegLeftTop.setRotationPoint(4.8F, -1.0F, -6.5F);
        frontLegLeftTop.addBox(-1.5F, -1.0F, -1.5F, 3, 7, 3, 0.2F);
        setRotation(frontLegLeftTop, 0.13962634015954636F, 0.0F, 0.0F);
        backLegLeftTop = new ModelRenderer(this, 109, 49);
        backLegLeftTop.setRotationPoint(4.5F, -2.5F, 12.0F);
        backLegLeftTop.addBox(-1.5F, 0.0F, -2.0F, 3, 10, 4, 0.05F);
        setRotation(backLegLeftTop, -0.10471975511965977F, 0.0F, 0.0F);
        frontLegRightTop = new ModelRenderer(this, 110, 38);
        frontLegRightTop.mirror = true;
        frontLegRightTop.setRotationPoint(-4.8F, -1.0F, -6.5F);
        frontLegRightTop.addBox(-1.5F, -1.0F, -1.5F, 3, 7, 3, 0.2F);
        setRotation(frontLegRightTop, 0.13962634015954636F, 0.0F, 0.0F);
        backLegRightTop = new ModelRenderer(this, 109, 49);
        backLegRightTop.setRotationPoint(-4.5F, -2.5F, 12.0F);
        backLegRightTop.addBox(-1.5F, 0.0F, -2.0F, 3, 10, 4, 0.05F);
        setRotation(backLegRightTop, -0.10471975511965977F, 0.0F, 0.0F);
        frontLegLeftMiddle = new ModelRenderer(this, 110, 13);
        frontLegLeftMiddle.setRotationPoint(-1.5F, 5.5F, -1.3F);
        frontLegLeftMiddle.addBox(0.0F, 0.0F, 0.0F, 3, 9, 3, 0.0F);
        setRotation(frontLegLeftMiddle, -0.19198621771937624F, 0.0F, 0.0F);
        frontLegRightMiddle = new ModelRenderer(this, 110, 13);
        frontLegRightMiddle.mirror = true;
        frontLegRightMiddle.setRotationPoint(-1.5F, 5.5F, -1.3F);
        frontLegRightMiddle.addBox(0.0F, 0.0F, 0.0F, 3, 9, 3, 0.0F);
        setRotation(frontLegRightMiddle, -0.19198621771937624F, 0.0F, 0.0F);
        backLegLeftMiddle = new ModelRenderer(this, 110, 26);
        backLegLeftMiddle.setRotationPoint(0.0F, 9.5F, -0.2F);
        backLegLeftMiddle.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(backLegLeftMiddle, 0.33161255787892263F, 0.0F, 0.0F);
        backLegRightMiddle = new ModelRenderer(this, 110, 26);
        backLegRightMiddle.setRotationPoint(0.0F, 9.5F, -0.2F);
        backLegRightMiddle.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(backLegRightMiddle, 0.33161255787892263F, 0.0F, 0.0F);
        frontLegRightBottom = new ModelRenderer(this, 112, 1);
        frontLegRightBottom.mirror = true;
        frontLegRightBottom.setRotationPoint(1.5F, 9.2F, 1.5F);
        frontLegRightBottom.addBox(-1.0F, -0.5F, -1.1F, 2, 10, 2, 0.0F);
        setRotation(frontLegRightBottom, 0.05235987755982988F, 0.0F, 0.0F);
        frontLegLeftBottom = new ModelRenderer(this, 112, 1);
        frontLegLeftBottom.setRotationPoint(1.5F, 9.2F, 1.5F);
        frontLegLeftBottom.addBox(-1.0F, -0.5F, -1.1F, 2, 10, 2, 0.0F);
        setRotation(frontLegLeftBottom, 0.05235987755982988F, 0.0F, 0.0F);
        backLegLeftBottom = new ModelRenderer(this, 112, 1);
        backLegLeftBottom.setRotationPoint(0.0F, 7.5F, 0.0F);
        backLegLeftBottom.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        setRotation(backLegLeftBottom, -0.22689280275926282F, 0.0F, 0.0F);
        backLegRightBottom = new ModelRenderer(this, 112, 1);
        backLegRightBottom.setRotationPoint(0.0F, 7.5F, 0.0F);
        backLegRightBottom.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        setRotation(backLegRightBottom, -0.22689280275926282F, 0.0F, 0.0F);

        chestRight = new ModelRenderer(this, 76, 0);
        chestRight.mirror = true;
        chestRight.setRotationPoint(-8.0F, -4.5F, 5.5F);
        chestRight.addBox(-0.2F, 0.0F, -4.0F, 3, 8, 8, 0.0F);
        setRotation(chestRight, 0.0F, 0.0F, -0.03490658503988659F);
        chestLeft = new ModelRenderer(this, 76, 0);
        chestLeft.setRotationPoint(5.0F, -4.5F, 5.5F);
        chestLeft.addBox(0.3F, 0.0F, -4.0F, 3, 8, 8, 0.0F);
        setRotation(chestLeft, 0.0F, 0.0F, 0.03490658503988659F);
        saddle = new ModelRenderer(this, 65, 17);
        saddle.setRotationPoint(0.0F, -11.0F, 2.0F);
        saddle.addBox(-3.5F, 1.05F, -7.2F, 7, 3, 15, 0.0F);
        saddlePostBack = new ModelRenderer(this, 0, 0);
        saddlePostBack.setRotationPoint(0.0F, 0.0F, 6.5F);
        saddlePostBack.addBox(-2.0F, -0.95F, -0.5F, 4, 2, 1, 0.0F);
        saddlePostFront = new ModelRenderer(this, 0, 0);
        saddlePostFront.setRotationPoint(0.0F, 0.0F, -6.0F);
        saddlePostFront.addBox(-0.5F, -1.95F, -0.4F, 1, 3, 1, 0.0F);
        reinsLeft = new ModelRenderer(this, 0, 4);
        reinsLeft.setRotationPoint(2.2F, -1.5F, 1.1F);
        reinsLeft.addBox(0.0F, 0.0F, 0.0F, 0, 3, 10, 0.0F);
        setRotation(reinsLeft, -0.13962634015954636F, 0.0F, 0.0F);
        reinsRight = new ModelRenderer(this, 0, 4);
        reinsRight.setRotationPoint(-2.2F, -1.5F, 1.1F);
        reinsRight.addBox(0.0F, 0.0F, 0.0F, 0, 3, 10, 0.0F);
        setRotation(reinsRight, -0.13962634015954636F, 0.0F, 0.0F);
        strapChestLeftAngle = new ModelRenderer(this, 0, 0);
        strapChestLeftAngle.setRotationPoint(3.2F, 3.3F, -6.0F);
        strapChestLeftAngle.addBox(0.0F, 0.0F, 0.0F, 0, 4, 1, 0.0F);
        setRotation(strapChestLeftAngle, 0.0F, 0.0F, -0.6108652381980153F);
        strapChestRightAngle = new ModelRenderer(this, 0, 0);
        strapChestRightAngle.setRotationPoint(-3.2F, 3.3F, -6.0F);
        strapChestRightAngle.addBox(0.0F, 0.0F, 0.0F, 0, 4, 1, 0.0F);
        setRotation(strapChestRightAngle, 0.0F, 0.0F, 0.6108652381980153F);
        strapBellyLeftAngle = new ModelRenderer(this, 0, 0);
        strapBellyLeftAngle.setRotationPoint(3.25F, 2.8F, 5.5F);
        strapBellyLeftAngle.addBox(0.0F, 0.0F, 0.0F, 0, 4, 1, 0.0F);
        setRotation(strapBellyLeftAngle, 0.0F, 0.0F, -0.3141592653589793F);
        strapBellyRightAngle = new ModelRenderer(this, 0, 0);
        strapBellyRightAngle.setRotationPoint(-3.25F, 2.8F, 5.5F);
        strapBellyRightAngle.addBox(0.0F, 0.0F, 0.0F, 0, 4, 1, 0.0F);
        setRotation(strapBellyRightAngle, 0.0F, 0.0F, 0.3141592653589793F);

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

        bodyFront = new ModelRenderer(this, 1, 34);
        bodyFront.setRotationPoint(-5.0F, 2.0F, -10.0F);
        bodyFront.addBox(0.0F, -7.0F, 0.0F, 10, 13, 17, scale);
        bodyBack = new ModelRenderer(this, 11, 13);
        bodyBack.setRotationPoint(0.5F, -6.0F, 6.5F);
        bodyBack.addBox(-4.5F, 1.05F, -0.5F, 8, 11, 9, scale);
        humpTop = new ModelRenderer(this, 69, 36);
        humpTop.setRotationPoint(0.0F, -7.0F, 3.0F);
        humpTop.addBox(-2.5F, -1.95F, -6.0F, 5, 2, 11, scale);
        humpBottom = new ModelRenderer(this, 59, 50);
        humpBottom.setRotationPoint(0.0F, -6.0F, 2.0F);
        humpBottom.addBox(-3.5F, -0.95F, -9.0F, 7, 2, 18, scale);

        saddle.addChild(strapBellyBottom);
        saddle.addChild(strapBellyRight);
        saddle.addChild(strapChestLeftAngle);
        saddle.addChild(strapBellyLeftAngle);
        saddle.addChild(strapChestRightAngle);
        saddle.addChild(strapBellyRightAngle);
        backLegLeftBottom.addChild(toesBackLeft);
        frontLegRightBottom.addChild(toesFrontRight);
        bodyBack.addChild(tail);
        backLegRightTop.addChild(backLegRightMiddle);
        snout.addChild(mandible);
        backLegLeftMiddle.addChild(backLegLeftBottom);
        saddle.addChild(saddlePostFront);
        saddle.addChild(strapChestLeft);
        backLegLeftTop.addChild(backLegLeftMiddle);
        backLegRightMiddle.addChild(backLegRightBottom);
        neckBase.addChild(neckUpper);
        head.addChild(earLeft);
        saddle.addChild(strapChestBottom);
        saddle.addChild(strapChestRight);
        saddle.addChild(strapBellyLeft);
        saddle.addChild(saddlePostBack);
        head.addChild(earRight);
        head.addChild(snout);
        frontLegLeftBottom.addChild(toesFrontLeft);
        backLegRightBottom.addChild(toesBackRight);
        neckUpper.addChild(head);
        head.addChild(bridleLeft2);
        head.addChild(bridleFront2);
        head.addChild(bridleBack2);
        head.addChild(bridleFront1);
        head.addChild(bridleFrontLeft1);
        head.addChild(bridleBack1);
        head.addChild(bridleRight1);
        head.addChild(bridleFrontBottom2);
        head.addChild(bridleRight2);
        head.addChild(bridleFrontRight1);
        head.addChild(bridleFrontBottom1);
        head.addChild(bridleLeft1);
        head.addChild(bridleFrontTop1);
        head.addChild(bridleFrontTop2);
        head.addChild(bridleFrontLeft2);
        head.addChild(bridleFrontRight2);
        head.addChild(reinsLeft);
        head.addChild(reinsRight);

        frontLegLeftTop.addChild(this.frontLegLeftMiddle);
        frontLegRightMiddle.addChild(this.frontLegRightBottom);
        frontLegLeftMiddle.addChild(this.frontLegLeftBottom);
        frontLegRightTop.addChild(this.frontLegRightMiddle);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        AbstractChestHorse abstractchesthorse = (AbstractChestHorse) entityIn;
        boolean flag1 = !abstractchesthorse.isChild() && abstractchesthorse.hasChest();
        boolean flag2 = !abstractchesthorse.isChild() && abstractchesthorse.isHorseSaddled();
        boolean flag3 = !abstractchesthorse.isChild() && abstractchesthorse.isBeingRidden();
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

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0D, 1.0D, 1.0D);

        neckBase.render(scale);
        frontLegLeftTop.render(scale);
        frontLegRightTop.render(scale);
        backLegLeftTop.render(scale);
        backLegRightTop.render(scale);
        bodyBack.render(scale);
        bodyFront.render(scale);
        humpBottom.render(scale);
        humpTop.render(scale);
        GlStateManager.popMatrix();

        bridleFront1.isHidden = true;
        bridleFront2.isHidden = true;
        bridleBack1.isHidden = true;
        bridleBack2.isHidden = true;
        bridleLeft1.isHidden = true;
        bridleLeft2.isHidden = true;
        bridleRight1.isHidden = true;
        bridleRight2.isHidden = true;
        bridleFrontTop1.isHidden = true;
        bridleFrontBottom1.isHidden = true;
        bridleFrontLeft1.isHidden = true;
        bridleFrontRight1.isHidden = true;
        bridleFrontTop2.isHidden = true;
        bridleFrontBottom2.isHidden = true;
        bridleFrontLeft2.isHidden = true;
        bridleFrontRight2.isHidden = true;
        reinsLeft.isHidden = true;
        reinsRight.isHidden = true;

        if (flag1)
        {
            chestLeft.render(scale);
            chestRight.render(scale);
        }

        if (flag2)
        {
            saddle.render(scale);
            bridleFront1.isHidden = false;
            bridleFront2.isHidden = false;
            bridleBack1.isHidden = false;
            bridleBack2.isHidden = false;
            bridleLeft1.isHidden = false;
            bridleLeft2.isHidden = false;
            bridleRight1.isHidden = false;
            bridleRight2.isHidden = false;
            bridleFrontTop1.isHidden = false;
            bridleFrontBottom1.isHidden = false;
            bridleFrontLeft1.isHidden = false;
            bridleFrontRight1.isHidden = false;
            bridleFrontTop2.isHidden = false;
            bridleFrontBottom2.isHidden = false;
            bridleFrontLeft2.isHidden = false;
            bridleFrontRight2.isHidden = false;
            strapChestRightAngle.isHidden = false;
            strapChestLeftAngle.isHidden = false;
            strapBellyRightAngle.isHidden = false;
            strapBellyLeftAngle.isHidden = false;
            reinsLeft.isHidden = true;
            reinsRight.isHidden = true;

            if (flag3)
            {
                reinsLeft.isHidden = false;
                reinsRight.isHidden = false;
            }

            if (flag1)//would preferably check for carpet
            {
                strapChestRightAngle.isHidden = true;
                strapChestLeftAngle.isHidden = true;
                strapBellyRightAngle.isHidden = true;
                strapBellyLeftAngle.isHidden = true;
            }
        }
        else if (abstractchesthorse instanceof EntityCamelTFC && ((EntityCamelTFC) abstractchesthorse).isHalter())
        {
            bridleFront1.isHidden = false;
            bridleFront2.isHidden = false;
            bridleBack1.isHidden = false;
            bridleBack2.isHidden = false;
            bridleLeft1.isHidden = false;
            bridleLeft2.isHidden = false;
            bridleRight1.isHidden = false;
            bridleRight2.isHidden = false;
            bridleFrontTop1.isHidden = false;
            bridleFrontBottom1.isHidden = false;
            bridleFrontLeft1.isHidden = false;
            bridleFrontRight1.isHidden = false;
            bridleFrontTop2.isHidden = false;
            bridleFrontBottom2.isHidden = false;
            bridleFrontLeft2.isHidden = false;
            bridleFrontRight2.isHidden = false;
        }
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        head.rotateAngleX = f4 / (180F / (float) Math.PI);
        head.rotateAngleY = f3 / (180F / (float) Math.PI);

        frontLegRightTop.rotateAngleX = MathHelper.cos(f * 0.4662F) * 1.0F * f1 + 0.13962634015954636F;
        frontLegLeftTop.rotateAngleX = MathHelper.cos(f * 0.4662F + (float) Math.PI) * 1.0F * f1 + 0.13962634015954636F;
        backLegLeftTop.rotateAngleX = MathHelper.cos(f * 0.4662F + (float) Math.PI) * 1.0F * f1 + -0.10471975511965977F;
        backLegRightTop.rotateAngleX = MathHelper.cos(f * 0.4662F) * 1.0F * f1 + -0.10471975511965977F;
    }


    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
        float f = this.updateHorseRotation(entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset, partialTickTime);
        float f1 = this.updateHorseRotation(entitylivingbaseIn.prevRotationYawHead, entitylivingbaseIn.rotationYawHead, partialTickTime);
        float f2 = entitylivingbaseIn.prevRotationPitch + (entitylivingbaseIn.rotationPitch - entitylivingbaseIn.prevRotationPitch) * partialTickTime;
        float f3 = f1 - f;
        float f4 = f2 * 0.017453292F;

        if (f3 > 20.0F) { f3 = 20.0F; }
        if (f3 < -20.0F) { f3 = -20.0F; }

        if (limbSwingAmount > 0.2F)
        {
            f4 += MathHelper.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount;
        }

        this.headNode.rotateAngleX = -0.2835988F + f4;
        this.headNode.rotateAngleY = f3 * -0.2835988F;
        this.headNode.rotateAngleX = (0.2617994F + f4) * 2.1816616F * this.headNode.rotateAngleX;
        this.headNode.rotateAngleY = f3 * 0.017453292F * this.headNode.rotateAngleY;

        this.neckBase.rotationPointY = this.headNode.rotationPointY;
        this.neckBase.rotationPointZ = this.headNode.rotationPointZ;
        this.neckBase.rotateAngleX = this.headNode.rotateAngleX;
    }


    private float updateHorseRotation(float f1, float f2, float f3)
    {
        float f = f2 - f1;

        while (f < -180.0F) { f += 360.0F; }
        while (f >= 180.0F) { f -= 360.0F; }

        return f1 + f3 * f;
    }


    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
